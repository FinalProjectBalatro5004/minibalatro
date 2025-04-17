import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Balatro from '../components/Balatro';

const AuthPage = () => {
  const [activeForm, setActiveForm] = useState('login');
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

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
      // Mock login for now
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Simple validation
      if (formData.username === 'demo' && formData.password === 'password') {
        // Successful login - redirect to game
        navigate('/game');
      } else {
        setError('Invalid username or password');
      }
    } catch (err) {
      setError('An error occurred during login');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSignupSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    // Basic validation
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }
    
    setLoading(true);
    
    try {
      // Mock signup for now
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Successful registration - redirect to game
      navigate('/game');
    } catch (err) {
      setError('An error occurred during registration');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const renderLoginForm = () => (
    <form onSubmit={handleLoginSubmit} className="space-y-4">
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
          placeholder="Enter your username"
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
          placeholder="Choose a username"
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

  const renderForgotPasswordForm = () => (
    <div className="space-y-4">
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
        type="button"
        className="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors"
      >
        Reset Password
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
    </div>
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
               activeForm === 'register' ? 'Create Account' : 'Reset Password'}
            </h1>
            <p className="text-gray-400 mt-2">
              {activeForm === 'login' ? 'Sign in to continue to Balatro' : 
               activeForm === 'register' ? 'Join the Balatro community' : 'We\'ll send you a reset link'}
            </p>
          </div>
          
          {error && (
            <div className="bg-red-900 bg-opacity-50 text-red-200 p-3 rounded-lg mb-4">
              {error}
            </div>
          )}
          
          {activeForm === 'login' && renderLoginForm()}
          {activeForm === 'register' && renderSignupForm()}
          {activeForm === 'forgot' && renderForgotPasswordForm()}
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