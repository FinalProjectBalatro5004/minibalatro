import { useEffect, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import SpotlightCard from './SpotlightCard';

/**
 * Toast component for displaying non-disruptive messages
 */
const Toast = ({ message, isVisible, onClose, duration = 3000, type = 'warning' }) => {
  const [localVisible, setLocalVisible] = useState(false);
  
  useEffect(() => {
    setLocalVisible(isVisible);
    
    if (isVisible && duration > 0) {
      const timer = setTimeout(() => {
        setLocalVisible(false);
        if (onClose) onClose();
      }, duration);
      
      return () => clearTimeout(timer);
    }
  }, [isVisible, duration, onClose]);
  
  // Different colors for different message types
  const getSpotlightColor = () => {
    switch (type) {
      case 'error':
        return 'rgba(255, 50, 50, 0.3)';
      case 'success':
        return 'rgba(50, 255, 50, 0.3)';
      case 'info':
        return 'rgba(50, 50, 255, 0.3)';
      case 'warning':
      default:
        return 'rgba(255, 220, 50, 0.3)';
    }
  };
  
  // Different icon for different message types
  const getIcon = () => {
    switch (type) {
      case 'error':
        return '❌';
      case 'success':
        return '✅';
      case 'info':
        return 'ℹ️';
      case 'warning':
      default:
        return '⚠️';
    }
  };

  return (
    <AnimatePresence>
      {localVisible && (
        <motion.div
          initial={{ opacity: 0, y: -50 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -50 }}
          className="fixed top-4 left-1/2 transform -translate-x-1/2 z-50"
        >
          <SpotlightCard 
            className="min-w-[300px] py-3" 
            spotlightColor={getSpotlightColor()}
          >
            <div className="flex items-center justify-between">
              <div className="flex items-center">
                <span className="mr-2 text-xl">{getIcon()}</span>
                <p className="text-white font-medium">{message}</p>
              </div>
              <button 
                onClick={() => {
                  setLocalVisible(false);
                  if (onClose) onClose();
                }}
                className="ml-4 text-neutral-500 hover:text-white transition-colors"
              >
                ✕
              </button>
            </div>
          </SpotlightCard>
        </motion.div>
      )}
    </AnimatePresence>
  );
};

export default Toast; 