import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Balatro from '../components/Balatro';
import { authService } from '../services/authService';

const AuthPage = () => {
  const [activeForm, setActiveForm] = useState('login');
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    confirmationCode: '',
    newPassword: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [pendingUsername, setPendingUsername] = useState('');

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const authResult = await authService.login(formData.username, formData.password);
      console.log('Login successful, auth result:', authResult);
      
      // Store tokens
      localStorage.setItem('accessToken', authResult.AccessToken);
      localStorage.setItem('idToken', authResult.IdToken);
      localStorage.setItem('username', formData.username);
      
      // Get and store user data
      const userData = await authService.getUserData(formData.username);
      console.log('User data retrieved:', userData);
      
      // Set highestChips from user data
      const highestChips = userData?.highestChips || 0;
      localStorage.setItem('highestChips', highestChips.toString());
      
      navigate('/game');
    } catch (err) {
      console.error('Login error:', err);
      setError(err.message || 'Invalid username or password');
    } finally {
      setLoading(false);
    }
  };

  const validatePassword = (password) => {
    const errors = [];
    if (password.length < 8) {
      errors.push('Password must be at least 8 characters long');
    }
    if (!/[a-z]/.test(password)) {
      errors.push('Password must contain at least one lowercase letter');
    }
    if (!/[A-Z]/.test(password)) {
      errors.push('Password must contain at least one uppercase letter');
    }
    if (!/[0-9]/.test(password)) {
      errors.push('Password must contain at least one number');
    }
    if (!/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
      errors.push('Password must contain at least one special character');
    }
    return errors;
  };

  const handleSignupSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    // Password validation
    const passwordErrors = validatePassword(formData.password);
    if (passwordErrors.length > 0) {
      setError(passwordErrors.join('. '));
      return;
    }
    
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }
    
    if (!authService.validateUsername(formData.username)) {
      setError('Username must be 8 characters or less and contain only letters and numbers');
      return;
    }
    
    setLoading(true);
    
    try {
      await authService.register(formData.username, formData.email, formData.password);
      setPendingUsername(formData.username);
      setSuccessMessage('Registration successful! Please check your email for the confirmation code.');
      setActiveForm('confirm');
      setFormData(prev => ({
        ...prev,
        email: '',
        password: '',
        confirmPassword: '',
        confirmationCode: ''
      }));
    } catch (err) {
      if (err.name === 'UsernameExistsException') {
        setError('This username is already taken. Please choose another one.');
      } else {
        setError(err.message || 'An error occurred during registration');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleConfirmationSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await authService.confirmRegistration(pendingUsername, formData.confirmationCode);
      setSuccessMessage('Account confirmed successfully! You can now login.');
      setActiveForm('login');
      setFormData({
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        confirmationCode: '',
        newPassword: ''
      });
    } catch (err) {
      setError(err.message || 'Invalid confirmation code');
    } finally {
      setLoading(false);
    }
  };

  const handleForgotPasswordSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await authService.forgotPassword(formData.username);
      setSuccessMessage('Password reset code sent to your email');
      setActiveForm('confirmForgotPassword');
    } catch (err) {
      setError(err.message || 'An error occurred while processing your request');
    } finally {
      setLoading(false);
    }
  };

  const handleConfirmForgotPasswordSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await authService.confirmForgotPassword(
        formData.username,
        formData.confirmationCode,
        formData.newPassword
      );
      setSuccessMessage('Password reset successful! You can now login with your new password.');
      setActiveForm('login');
    } catch (err) {
      setError(err.message || 'Invalid confirmation code');
    } finally {
      setLoading(false);
    }
  };

  const renderLoginForm = () => (
    <form onSubmit={handleLoginSubmit} className="space-y-4">
      <div className="mb-4">
        <label className="block text-gray-300 text-sm font-bold mb-2" htmlFor="username">
          Username
        </label>
        <input
          className="w-full px-4 py-2 bg-gray-800 border border-gray-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          id="username"
          type="text"
          placeholder="Enter your username"
          value={formData.username}
          onChange={(e) => setFormData({ ...formData, username: e.target.value })}
        />
      </div>
      
      <div>
        <label htmlFor="password" className="block text-gray-300 mb-1">Password</label>
        <input
          type="password"
          id="password"
          name="password"
          value={formData.password}
          onChange={handleChange}
          required
          className="w-full px-4 py-2 bg-gray-800 border border-gray-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Enter your password"
        />
      </div>
      
      <div className="text-right">
        <button 
          type="button" 
          onClick={() => setActiveForm('forgot')}
          className="text-blue-400 hover:text-blue-300 text-sm"
        >
          Forgot Password?
        </button>
      </div>
      
      <button
        type="submit"
        disabled={loading}
        className="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors disabled:opacity-50"
      >
        {loading ? 'Signing in...' : 'Sign In'}
      </button>
      
      <div className="text-center mt-4">
        <p className="text-gray-400">
          Don't have an account?{' '}
          <button
            type="button"
            onClick={() => setActiveForm('register')}
            className="text-blue-400 hover:text-blue-300"
          >
            Sign Up
          </button>
        </p>
      </div>
    </form>
  );

  const renderSignupForm = () => (
    <form onSubmit={handleSignupSubmit} className="space-y-4">
      <div>
        <label htmlFor="username" className="block text-gray-300 mb-1">Username</label>
        <input
          type="text"
          id="username"
          name="username"
          value={formData.username}
          onChange={handleChange}
          required
          className="w-full px-4 py-2 bg-gray-800 border border-gray-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Choose a username (max 8 characters)"
        />
      </div>
      
      <div>
        <label htmlFor="email" className="block text-gray-300 mb-1">Email</label>
        <input
          type="email"
          id="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          required
          className="w-full px-4 py-2 bg-gray-800 border border-gray-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Enter your email"
        />
      </div>
      
      <div>
        <label htmlFor="password" className="block text-gray-300 mb-1">Password</label>
        <input
          type="password"
          id="password"
          name="password"
          value={formData.password}
          onChange={handleChange}
          required
          className="w-full px-4 py-2 bg-gray-800 border border-gray-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Create a password"
        />
        <p className="text-sm text-gray-400 mt-1">
          Password must contain:
          <ul className="list-disc list-inside">
            <li>At least 8 characters</li>
            <li>At least one uppercase letter</li>
            <li>At least one lowercase letter</li>
            <li>At least one number</li>
            <li>At least one special character</li>
          </ul>
        </p>
      </div>
      
      <div>
        <label htmlFor="confirmPassword" className="block text-gray-300 mb-1">Confirm Password</label>
        <input
          type="password"
          id="confirmPassword"
          name="confirmPassword"
          value={formData.confirmPassword}
          onChange={handleChange}
          required
          className="w-full px-4 py-2 bg-gray-800 border border-gray-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Confirm your password"
        />
      </div>
      
      <button
        type="submit"
        disabled={loading}
        className="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors disabled:opacity-50"
      >
        {loading ? 'Creating Account...' : 'Create Account'}
      </button>
      
      <div className="text-center mt-4">
        <p className="text-gray-400">
          Already have an account?{' '}
          <button
            type="button"
            onClick={() => setActiveForm('login')}
            className="text-blue-400 hover:text-blue-300"
          >
            Sign In
          </button>
        </p>
      </div>
    </form>
  );

  const renderConfirmationForm = () => (
    <form onSubmit={handleConfirmationSubmit} className="space-y-4">
      <div>
        <label htmlFor="confirmationCode" className="block text-gray-300 mb-1">Confirmation Code</label>
        <input
          type="text"
          id="confirmationCode"
          name="confirmationCode"
          value={formData.confirmationCode}
          onChange={handleChange}
          required
          className="w-full px-4 py-2 bg-gray-800 border border-gray-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Enter the confirmation code from your email"
        />
      </div>
      
      <button
        type="submit"
        disabled={loading}
        className="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors disabled:opacity-50"
      >
        {loading ? 'Confirming...' : 'Confirm Account'}
      </button>
      
      <div className="text-center mt-4">
        <p className="text-gray-400">
          Didn't receive a confirmation code?{' '}
          <button
            type="button"
            onClick={() => setActiveForm('register')}
            className="text-blue-400 hover:text-blue-300"
          >
            Try Again
          </button>
        </p>
      </div>
    </form>
  );

  const renderForgotPasswordForm = () => (
    <form onSubmit={handleForgotPasswordSubmit} className="space-y-4">
      <div>
        <label htmlFor="email" className="block text-gray-300 mb-1">Email</label>
        <input
          type="email"
          id="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          required
          className="w-full px-4 py-2 bg-gray-800 border border-gray-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Enter your email"
        />
      </div>
      
      <button
        type="submit"
        disabled={loading}
        className="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors disabled:opacity-50"
      >
        {loading ? 'Sending Reset Code...' : 'Reset Password'}
      </button>
      
      <div className="text-center mt-4">
        <button
          type="button"
          onClick={() => setActiveForm('login')}
          className="text-blue-400 hover:text-blue-300"
        >
          Back to Sign In
        </button>
      </div>
    </form>
  );

  const renderConfirmForgotPasswordForm = () => (
    <form onSubmit={handleConfirmForgotPasswordSubmit} className="space-y-4">
      <div>
        <label htmlFor="confirmationCode" className="block text-gray-300 mb-1">Confirmation Code</label>
        <input
          type="text"
          id="confirmationCode"
          name="confirmationCode"
          value={formData.confirmationCode}
          onChange={handleChange}
          required
          className="w-full px-4 py-2 bg-gray-800 border border-gray-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Enter the confirmation code"
        />
      </div>
      
      <div>
        <label htmlFor="newPassword" className="block text-gray-300 mb-1">New Password</label>
        <input
          type="password"
          id="newPassword"
          name="newPassword"
          value={formData.newPassword}
          onChange={handleChange}
          required
          className="w-full px-4 py-2 bg-gray-800 border border-gray-700 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Enter your new password"
        />
      </div>
      
      <button
        type="submit"
        disabled={loading}
        className="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors disabled:opacity-50"
      >
        {loading ? 'Confirming...' : 'Confirm New Password'}
      </button>
      
      <div className="text-center mt-4">
        <button
          type="button"
          onClick={() => setActiveForm('login')}
          className="text-blue-400 hover:text-blue-300"
        >
          Back to Sign In
        </button>
      </div>
    </form>
  );

  return (
    <div className="relative min-h-screen w-full overflow-hidden">
      {/* Balatro Background with different settings for the auth page */}
      <div className="absolute inset-0 z-0">
        <Balatro
          isRotate={false}
          mouseInteraction={true}
          pixelFilter={700}
          color1="#4338ca" // Indigo
          color2="#9333ea" // Purple
        />
      </div>
      
      {/* Content */}
      <div className="relative z-10 flex flex-col items-center justify-center min-h-screen px-4">
        <div className="bg-gray-900 bg-opacity-90 p-8 rounded-lg max-w-md w-full backdrop-blur-md shadow-xl">
          <div className="text-center mb-8">
            <h1 className="text-3xl font-bold text-white">
              {activeForm === 'login' ? 'Welcome Back' : 
               activeForm === 'register' ? 'Create Account' :
               activeForm === 'confirm' ? 'Confirm Account' :
               activeForm === 'forgot' ? 'Reset Password' : 'Confirm New Password'}
            </h1>
            <p className="text-gray-400 mt-2">
              {activeForm === 'login' ? 'Sign in to continue to Balatro' : 
               activeForm === 'register' ? 'Join the Balatro community' :
               activeForm === 'confirm' ? 'Confirm your account to complete registration' :
               activeForm === 'forgot' ? 'We\'ll send you a reset link' : 'Confirm your new password'}
            </p>
          </div>
          
          {error && (
            <div className="bg-red-900 bg-opacity-50 text-red-200 p-3 rounded-lg mb-4">
              {error}
            </div>
          )}
          
          {successMessage && (
            <div className="bg-green-900 bg-opacity-50 text-green-200 p-3 rounded-lg mb-4">
              {successMessage}
            </div>
          )}
          
          {activeForm === 'login' && renderLoginForm()}
          {activeForm === 'register' && renderSignupForm()}
          {activeForm === 'confirm' && renderConfirmationForm()}
          {activeForm === 'forgot' && renderForgotPasswordForm()}
          {activeForm === 'confirmForgotPassword' && renderConfirmForgotPasswordForm()}
        </div>
        
        {/* Back to home link */}
        <div className="mt-6">
          <button 
            onClick={() => navigate('/')}
            className="text-gray-400 hover:text-white transition-colors"
          >
            ← Back to Home
          </button>
        </div>
      </div>
    </div>
  );
};

export default AuthPage; 