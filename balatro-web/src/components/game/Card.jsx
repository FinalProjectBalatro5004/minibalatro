import React from 'react';

const Card = ({ rank, suit, hidden, onClick, selected, disabled }) => {
  const getSuitSymbol = (suit) => {
    switch (suit?.toLowerCase()) {
      case 'hearts': return '♥';
      case 'diamonds': return '♦';
      case 'clubs': return '♣';
      case 'spades': return '♠';
      default: return suit;
    }
  };
  
  const getSuitColor = (suit) => {
    return suit?.toLowerCase() === 'hearts' || suit?.toLowerCase() === 'diamonds' 
      ? 'text-red-500' 
      : 'text-black';
  };

  const getCardGradient = (suit) => {
    if (!suit) return 'bg-gray-300';
    
    switch (suit.toLowerCase()) {
      case 'hearts':
        return selected ? 'bg-gradient-to-br from-red-50 to-red-100' : 'bg-gradient-to-br from-white to-red-50';
      case 'diamonds':
        return selected ? 'bg-gradient-to-br from-red-50 to-pink-100' : 'bg-gradient-to-br from-white to-pink-50';
      case 'clubs':
        return selected ? 'bg-gradient-to-br from-blue-50 to-slate-100' : 'bg-gradient-to-br from-white to-slate-50';
      case 'spades':
        return selected ? 'bg-gradient-to-br from-blue-50 to-indigo-100' : 'bg-gradient-to-br from-white to-indigo-50';
      default:
        return 'bg-white';
    }
  };

  return (
    <div 
      className={`relative w-20 h-32 rounded-xl shadow-lg flex flex-col items-center justify-center 
        transition-all duration-200 
        ${disabled ? 'cursor-default opacity-90 scale-90' : 'cursor-pointer'}
        ${selected && !disabled ? 'transform -translate-y-4' : ''}
        ${!disabled && !selected ? 'hover:transform hover:-translate-y-2' : ''}`}
      style={{ 
        backgroundColor: hidden ? '#1e293b' : 'white',
        boxShadow: selected && !disabled
          ? '0 10px 15px -3px rgba(0, 255, 0, 0.2), 0 4px 6px -4px rgba(0, 255, 0, 0.2), 0 0 0 2px rgba(0, 255, 0, 0.4)' 
          : '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1)',
      }}
      onClick={disabled ? undefined : onClick}
    >
      {!hidden && (
        <div className={`w-full h-full rounded-xl overflow-hidden ${getCardGradient(suit)} flex flex-col items-center justify-center p-1`}>
          {/* Card Corner - Top Left */}
          <div className={`absolute top-1 left-2 flex flex-col items-center ${getSuitColor(suit)} font-bold`}>
            <div>{rank}</div>
            <div className="text-sm">{getSuitSymbol(suit)}</div>
          </div>
          
          {/* Card Center */}
          <div className={`text-5xl ${getSuitColor(suit)} transform ${selected && !disabled ? 'scale-110' : ''} transition-transform`}>
            {getSuitSymbol(suit)}
          </div>
          
          {/* Card Corner - Bottom Right */}
          <div className={`absolute bottom-1 right-2 flex flex-col items-center ${getSuitColor(suit)} font-bold`}>
            <div className="text-sm">{getSuitSymbol(suit)}</div>
            <div>{rank}</div>
          </div>
          
          {/* Selection indicator */}
          {selected && !disabled && (
            <div className="absolute -bottom-1 left-0 right-0 h-1 bg-green-400 rounded-b-xl"></div>
          )}
        </div>
      )}
    </div>
  );
};

export default Card; 