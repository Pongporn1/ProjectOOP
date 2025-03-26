'use client'
import { motion } from 'framer-motion'
import { ReactNode, useEffect } from 'react';
import { selectUser } from "@/stores/slices/userSlice";
import {  usePathname, useRouter } from 'next/navigation';
import { useAppSelector } from '@/stores/hook';

const variants = {
  hidden: { opacity: 0, x: 0, y: 0 },
  enter: { opacity: 1, x: 0, y: 0 },
}


export default function Template({ children }: {children: ReactNode}) {
  const pathname = usePathname();
  
  const user = useAppSelector(selectUser)
  const router = useRouter()
  const available = (pathname === "/" || user !== null)


  useEffect(() => {
    let data = setTimeout(() => {
      if(!available){
        router.push('/')
      }
    }, 0);
    return () => clearTimeout(data);
  }, []);

  return (
    <main>
      <div className="h-screen w-screen fixed noise"/>
    {available && children}
    <motion.div
      className="slide-in z-20"
      initial={{ scaleY: 0 }}
      animate={{ scaleY: 0 }}
      exit={{ scaleY: 1 }}
      transition={{ duration: 0.6, ease: [0.22, 1, 0.36, 1] }}
    />
    <motion.div
      className="slide-out z-20"
      initial={{ scaleY: 1 }}
      animate={{ scaleY: 0 }}
      exit={{ scaleY: 0 }}
      transition={{ duration: 0.6, ease: [0.22, 1, 0.36, 1] }}
    />
    
  </main>
  )
}