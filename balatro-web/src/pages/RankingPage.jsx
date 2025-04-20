import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Balatro from '../components/Balatro';
import { authService } from '../services/authService';

const RankingPage = () => {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const userData = await authService.getAllUsers();
      // Sort users by highest chips in descending order
      const sortedUsers = userData.sort((a, b) => b.highestChips - a.highestChips);
      setUsers(sortedUsers);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching user rankings:', error);
      setLoading(false);
    }
  };

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
          {/* Header with title and buttons */}
          <div className="flex items-center mb-8">
            <h1 className="text-4xl font-bold text-white">
              Mini <span className="text-red-500">Balatro</span> Rankings
            </h1>
            {/* Push buttons to the far right with flex-grow and add more spacing */}
            <div className="flex-grow min-w-[50px]"></div> {/* Increase minimum width for more spacing */}
            <div className="flex gap-3">
              <button 
                onClick={() => navigate('/')}
                className="px-4 py-2 bg-gray-700 hover:bg-gray-600 rounded-lg transition-colors shadow-md text-white"
              >
                Home
              </button>
              <button 
                onClick={() => navigate('/game')}
                className="px-4 py-2 bg-blue-600 hover:bg-blue-700 rounded-lg transition-colors shadow-md text-white"
              >
                Play Game
              </button>
            </div>
          </div>
          
          {loading ? (
            <div className="text-center p-8">
              <p className="text-xl text-gray-300">Loading rankings...</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-gray-700">
                    <th className="py-4 text-center w-28">Rank</th>
                    <th className="py-4 text-center">Player</th>
                    {/* Changed to center alignment for the header */}
                    <th className="py-4 text-center w-36">Highest Chips</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((user, index) => (
                    <tr key={user.username} className="border-b border-gray-700">
                      <td className={`py-4 text-center ${
                        index === 0 ? 'text-yellow-400' : 
                        index === 1 ? 'text-gray-300' : 
                        index === 2 ? 'text-amber-600' : 'text-white'
                      }`}>
                        {index === 0 && "🥇"}
                        {index === 1 && "🥈"}
                        {index === 2 && "🥉"}
                        {index > 2 && `#${index + 1}`}
                      </td>
                      <td className="py-4 text-center text-white">{user.username}</td>
                      {/* Changed to text-center for consistency */}
                      <td className="py-4 text-center text-yellow-400 font-bold">{user.highestChips}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default RankingPage;