import {
  SignUpCommand,
  ConfirmSignUpCommand,
  InitiateAuthCommand,
  ForgotPasswordCommand,
  ConfirmForgotPasswordCommand,
} from "@aws-sdk/client-cognito-identity-provider";
import { PutCommand, GetCommand, UpdateCommand } from "@aws-sdk/lib-dynamodb";
import {
  cognitoClient,
  docClient,
  USER_POOL_ID,
  CLIENT_ID,
  TABLE_NAME,
} from "../config/aws";

// Username validation regex (8 characters or less, alphanumeric only)
const USERNAME_REGEX = /^[a-zA-Z0-9]{1,8}$/;

// Function to calculate secret hash using Web Crypto API
const calculateSecretHash = async (username) => {
  const message = username + CLIENT_ID;
  const key = import.meta.env.VITE_AWS_COGNITO_CLIENT_SECRET;
  
  console.log('Calculating secret hash with:', {
    username,
    clientId: CLIENT_ID,
    message,
    key: key ? 'present' : 'missing'
  });
  
  // Convert key to ArrayBuffer
  const encoder = new TextEncoder();
  const keyData = encoder.encode(key);
  const messageData = encoder.encode(message);
  
  // Import key
  const cryptoKey = await window.crypto.subtle.importKey(
    'raw',
    keyData,
    { name: 'HMAC', hash: 'SHA-256' },
    false,
    ['sign']
  );
  
  // Sign the message
  const signature = await window.crypto.subtle.sign(
    'HMAC',
    cryptoKey,
    messageData
  );
  
  // Convert to base64
  const secretHash = btoa(String.fromCharCode(...new Uint8Array(signature)));
  
  console.log('Secret hash calculated:', secretHash);
  return secretHash;
};

export const authService = {
  // Validate username format
  validateUsername(username) {
    return USERNAME_REGEX.test(username);
  },

  // Register new user
  async register(username, email, password) {
    if (!this.validateUsername(username)) {
      throw new Error("Username must be 8 characters or less and contain only letters and numbers");
    }

    try {
      console.log('Starting registration process...');
      const secretHash = await calculateSecretHash(username);
      console.log('Secret hash calculated');

      // Register user in Cognito
      const signUpCommand = new SignUpCommand({
        ClientId: CLIENT_ID,
        Username: username,
        Password: password,
        SecretHash: secretHash,
        UserAttributes: [
          { Name: "email", Value: email },
        ],
      });

      console.log('Sending signup command to Cognito...');
      const cognitoResponse = await cognitoClient.send(signUpCommand);
      console.log('Cognito registration successful');

      try {
        // Create user record in DynamoDB
        const putCommand = new PutCommand({
          TableName: TABLE_NAME,
          Item: {
            username,
            email,
            highestChips: 0,
            createdAt: new Date().toISOString(),
          },
        });

        console.log('Creating user record in DynamoDB...');
        await docClient.send(putCommand);
        console.log('DynamoDB record created successfully');
      } catch (dynamoError) {
        console.error('Error creating DynamoDB record:', dynamoError);
        // Don't throw the error here, as the user is already registered in Cognito
      }

      return cognitoResponse;
    } catch (error) {
      console.error('Registration error details:', {
        name: error.name,
        message: error.message,
        stack: error.stack
      });
      throw error;
    }
  },

  // Confirm user registration
  async confirmRegistration(username, code) {
    try {
      console.log('Starting confirmation process...');
      const secretHash = await calculateSecretHash(username);
      console.log('Secret hash calculated');

      const command = new ConfirmSignUpCommand({
        ClientId: CLIENT_ID,
        Username: username,
        ConfirmationCode: code,
        SecretHash: secretHash,
      });

      console.log('Sending confirmation command to Cognito...');
      const response = await cognitoClient.send(command);
      console.log('Confirmation successful');
      return response;
    } catch (error) {
      console.error('Confirmation error details:', {
        name: error.name,
        message: error.message,
        stack: error.stack
      });
      throw error;
    }
  },

  // Login user
  async login(username, password) {
    try {
      const secretHash = await calculateSecretHash(username);
      
      const command = new InitiateAuthCommand({
        AuthFlow: "USER_PASSWORD_AUTH",
        ClientId: CLIENT_ID,
        AuthParameters: {
          USERNAME: username,
          PASSWORD: password,
          SECRET_HASH: secretHash,
        },
      });

      const response = await cognitoClient.send(command);
      
      // Store tokens
      localStorage.setItem('accessToken', response.AuthenticationResult.AccessToken);
      localStorage.setItem('idToken', response.AuthenticationResult.IdToken);
      localStorage.setItem('refreshToken', response.AuthenticationResult.RefreshToken);
      localStorage.setItem('username', username);
      
      // Get user data from DynamoDB
      const userData = await this.getUserData(username);
      if (userData && userData.highestChips) {
        localStorage.setItem('highestChips', userData.highestChips.toString());
      } else {
        localStorage.setItem('highestChips', '0');
      }
      
      return response;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  },

  // Forgot password
  async forgotPassword(username) {
    try {
      const secretHash = await calculateSecretHash(username);
      
      const command = new ForgotPasswordCommand({
        ClientId: CLIENT_ID,
        Username: username,
        SecretHash: secretHash,
      });

      await cognitoClient.send(command);
    } catch (error) {
      console.error("Forgot password error:", error);
      throw error;
    }
  },

  // Confirm forgot password
  async confirmForgotPassword(username, code, newPassword) {
    try {
      const secretHash = await calculateSecretHash(username);
      
      const command = new ConfirmForgotPasswordCommand({
        ClientId: CLIENT_ID,
        Username: username,
        ConfirmationCode: code,
        Password: newPassword,
        SecretHash: secretHash,
      });

      await cognitoClient.send(command);
    } catch (error) {
      console.error("Confirm forgot password error:", error);
      throw error;
    }
  },

  // Get user data
  async getUserData(username) {
    try {
      console.log('Starting to get user data for username:', username);
      console.log('Using DynamoDB table:', TABLE_NAME);
      console.log('AWS Region:', import.meta.env.VITE_AWS_REGION || 'us-east-1');
      
      const command = new GetCommand({
        TableName: TABLE_NAME,
        Key: { username },
      });

      console.log('Sending get command to DynamoDB with params:', {
        TableName: TABLE_NAME,
        Key: { username },
        Region: import.meta.env.VITE_AWS_REGION || 'us-east-1'
      });

      const response = await docClient.send(command);
      console.log('DynamoDB response:', response);

      if (!response.Item) {
        console.log('No user data found for username:', username);
        return null;
      }

      console.log('User data retrieved successfully:', response.Item);
      return response.Item;
    } catch (error) {
      console.error('Get user data error details:', {
        name: error.name,
        message: error.message,
        stack: error.stack,
        username: username,
        tableName: TABLE_NAME,
        region: import.meta.env.VITE_AWS_REGION || 'us-east-1',
        credentials: {
          accessKeyId: import.meta.env.VITE_AWS_ACCESS_KEY_ID ? 'present' : 'missing',
          secretAccessKey: import.meta.env.VITE_AWS_SECRET_ACCESS_KEY ? 'present' : 'missing',
          region: import.meta.env.VITE_AWS_REGION || 'us-east-1'
        }
      });
      
      // Handle specific error cases
      if (error.name === 'UnrecognizedClientException') {
        throw new Error('AWS credentials are invalid. Please check your .env file.');
      } else if (error.name === 'ResourceNotFoundException') {
        console.log('DynamoDB table not found:', TABLE_NAME);
        throw new Error('DynamoDB table does not exist. Please create the table first.');
      } else if (error.name === 'AccessDeniedException') {
        throw new Error('Access denied to DynamoDB. Please check IAM permissions.');
      } else if (error.name === 'ValidationException') {
        throw new Error('Invalid request parameters. Please check the table name and key format.');
      } else {
        throw error;
      }
    }
  },

  // Update user's highest chips
  async updateHighestChips(username, chips) {
    try {
      const command = new UpdateCommand({
        TableName: TABLE_NAME,
        Key: { username },
        UpdateExpression: "SET highestChips = :chips",
        ExpressionAttributeValues: {
          ":chips": chips,
        },
        ReturnValues: "ALL_NEW",
      });

      const response = await docClient.send(command);
      return response.Attributes;
    } catch (error) {
      console.error("Update highest chips error:", error);
      throw error;
    }
  },

  // 更新用户数据
  async updateUserData(username, data) {
    try {
      const params = {
        TableName: TABLE_NAME,
        Key: {
          username: username
        },
        UpdateExpression: 'set highestChips = :highestChips',
        ExpressionAttributeValues: {
          ':highestChips': data.highestChips
        },
        ReturnValues: 'UPDATED_NEW'
      };

      await docClient.send(new UpdateCommand(params));
      return true;
    } catch (error) {
      console.error('Error updating user data:', error);
      throw error;
    }
  },

  // Logout user
  logout() {
    try {
      // Clear all authentication-related data from localStorage
      localStorage.removeItem('accessToken');
      localStorage.removeItem('idToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('username');
      localStorage.removeItem('highestChips');
      
      console.log('User logged out successfully');
      return true;
    } catch (error) {
      console.error('Logout error:', error);
      throw error;
    }
  },

  // 测试数据库操作
  async testDatabase(username) {
    try {
      // 获取用户数据
      const userData = await this.getUserData(username);
      console.log('Current user data:', userData);

      // 更新最高chips
      const testChips = 1000;
      await this.updateUserData(username, { highestChips: testChips });
      console.log('Updated highest chips to:', testChips);

      // 再次获取用户数据验证更新
      const updatedData = await this.getUserData(username);
      console.log('Updated user data:', updatedData);

      return updatedData;
    } catch (error) {
      console.error('Database test error:', error);
      throw error;
    }
  },

  // Get all users for ranking
  async getAllUsers() {
    try {
      // For a small application, we can use a scan operation to get all users
      const { ScanCommand } = await import("@aws-sdk/lib-dynamodb");
      
      const command = new ScanCommand({
        TableName: TABLE_NAME,
        ProjectionExpression: "username, highestChips, createdAt"
      });
      
      const response = await docClient.send(command);
      console.log('Got all users from DynamoDB:', response.Items);
      
      return response.Items || [];
    } catch (error) {
      console.error('Error fetching all users:', error);
      throw error;
    }
  }
}; 