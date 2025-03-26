'use client';
import { motion, useInView } from 'framer-motion';
import * as React from 'react';
 
export function TextFade({
  direction,
  children,
  isShow,
  childClass = '',
  className = '',
  staggerChildren = 0.1,
}: {
  direction: 'up' | 'down';
  children: React.ReactNode;
  isShow: boolean
  childClass?: string; 
  className?: string;
  staggerChildren?: number;
}) {
  const FADE_DOWN = {
    show: { opacity: 1, y: 0, transition: { type: 'spring' } },
    hidden: { opacity: 0, y: direction === 'down' ? -18 : 18 },
  };
  const ref = React.useRef(null);
  const isInView = useInView(ref, { once: true });
  return (
    <motion.div
      ref={ref}
      initial="hidden"
      animate={isShow ? 'show' : 'hidden'}
      variants={{
        hidden: {          
            transition: {
            staggerChildren: staggerChildren,
            staggerDirection: -1,
          },},
        show: {
          transition: {
            staggerChildren: staggerChildren,
            delayChildren: 0.6
          },
        },
      }}
      className={className}
    >
      {React.Children.map(children, (child) =>
        React.isValidElement(child) ? (
          <motion.div className={childClass} variants={FADE_DOWN}>{child}</motion.div>
        ) : (
          child
        )
      )}
    </motion.div>
  );
}