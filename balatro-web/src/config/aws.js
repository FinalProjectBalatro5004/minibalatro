import { CognitoIdentityProviderClient } from "@aws-sdk/client-cognito-identity-provider";
import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient } from "@aws-sdk/lib-dynamodb";

// AWS Region - make sure this matches your Cognito User Pool and DynamoDB table region
const REGION = "us-east-1";

// Debug AWS credentials
console.log('AWS Credentials Debug:', {
  accessKeyId: import.meta.env.VITE_AWS_ACCESS_KEY_ID,
  secretAccessKey: import.meta.env.VITE_AWS_SECRET_ACCESS_KEY ? '***' : 'missing',
  region: REGION
});

// AWS Credentials
const credentials = {
  accessKeyId: import.meta.env.VITE_AWS_ACCESS_KEY_ID,
  secretAccessKey: import.meta.env.VITE_AWS_SECRET_ACCESS_KEY,
};

// Validate credentials
if (!credentials.accessKeyId || !credentials.secretAccessKey) {
  console.error("AWS credentials are missing. Please check your .env file.");
  throw new Error("AWS credentials are missing");
}

// Cognito Configuration
// 请替换为你的实际 User Pool ID 和 Client ID
export const USER_POOL_ID = "us-east-1_6ZIYEYmP4"; // 替换为你的 User Pool ID
export const CLIENT_ID = "497eup0hla2movs600m1kdpm16"; // 替换为你的 Client ID

// DynamoDB Configuration
export const TABLE_NAME = "BalatroUsers";

// Create AWS clients with consistent region and credentials
export const cognitoClient = new CognitoIdentityProviderClient({
  region: REGION,
  credentials,
});

export const dynamoClient = new DynamoDBClient({
  region: REGION,
  credentials,
});

export const docClient = DynamoDBDocumentClient.from(dynamoClient, {
  marshallOptions: {
    removeUndefinedValues: true,
  },
}); 