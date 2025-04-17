import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import AuthPage from './pages/AuthPage';
import GamePage from './pages/GamePage';
import './App.css'

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/auth" element={<AuthPage />} />
        <Route path="/game" element={<GamePage />} />
        {/* Add more routes as needed */}
        <Route path="*" element={
          <div className="flex items-center justify-center min-h-screen bg-gray-900 text-white">
            <div className="text-center">
              <h1 className="text-4xl font-bold mb-4">404 - Page Not Found</h1>
              <p className="mb-8">The page you're looking for doesn't exist.</p>
              <a 
                href="/" 
                className="px-6 py-2 bg-blue-600 hover:bg-blue-700 rounded-lg transition-colors"
              >
                Go Home
              </a>
            </div>
          </div>
        } />
      </Routes>
    </Router>
  );
}

export default App;
