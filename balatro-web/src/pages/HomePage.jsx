import React from 'react';
import { Link } from 'react-router-dom';
import Balatro from '../components/Balatro';
import { useNavigate } from 'react-router-dom';

const HomePage = () => {
  const navigate = useNavigate();

  return (
    <div className="relative min-h-screen w-full overflow-hidden">
      {/* Balatro Background */}
      <div className="absolute inset-0 z-0">
        <Balatro
          isRotate={false}
          mouseInteraction={true}
          pixelFilter={700}
        />
      </div>
      
      {/* Content */}
      <div className="relative z-10 flex flex-col items-center justify-center min-h-screen px-4 text-center">
        <div className="bg-black bg-opacity-50 p-8 rounded-lg max-w-3xl mx-auto backdrop-blur-md">
          <h1 className="text-6xl font-bold mb-4 text-white">
            Mini  <span className="text-red-500">Balatro</span>
          </h1>
          <p className="text-xl text-gray-200 mb-8">
            A unique poker-inspired roguelike game with deck-building elements.
          </p>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
            <div className="p-4 bg-gray-800 bg-opacity-75 rounded-lg">
              <h2 className="text-2xl font-bold text-white mb-2">Game Features</h2>
              <ul className="text-left text-gray-200 space-y-2">
                <li className="flex items-center">
                  <span className="mr-2">🃏</span> Unique card combinations
                </li>
                <li className="flex items-center">
                  <span className="mr-2">🎭</span> Special joker cards
                </li>
                <li className="flex items-center">
                  <span className="mr-2">🎮</span> Strategic gameplay
                </li>
                
              </ul>
            </div>
            
            <div className="p-4 bg-gray-800 bg-opacity-75 rounded-lg">
              <h2 className="text-2xl font-bold text-white mb-2">Online Features</h2>
              <ul className="text-left text-gray-200 space-y-2">
                <li className="flex items-center">
                  <span className="mr-2">👥</span> Player profiles
                </li>
                <li className="flex items-center">
                  <span className="mr-2">🏆</span> Global leaderboards
                </li>
                <li className="flex items-center">
                  <span className="mr-2">🔄</span> Sync progress
                </li>
                
              </ul>
            </div>
          </div>
          
          <div className="flex flex-col items-center gap-4">
            <button
              onClick={() => navigate('/auth')}
              className="px-8 py-3 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors shadow-lg"
            >
              Sign In
            </button>
          </div>
          
          {/* Reference Citation */}
          <div className="mt-8 text-gray-400 text-xs">
            <p>
              Reference: Balatro. (n.d.). Steam Store Page. Retrieved from{" "}
              <a 
                href="https://store.steampowered.com/app/2379780/Balatro/" 
                target="_blank" 
                rel="noopener noreferrer"
                className="text-blue-400 hover:underline"
              >
                https://store.steampowered.com/app/2379780/Balatro/
              </a>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HomePage; 