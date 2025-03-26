"use client";

import React from "react";
import "./globals.css";

import { Provider as ReduxProvider } from "react-redux";
import store from "@/stores/store";
import WebSocketProvider from "@/providers/WebsocketProvider";

import PageAnimatePresence from "@/app/components/HOC/PageAnimatePresence";


import { Inter, Roboto_Mono } from "next/font/google";
import { motion } from "framer-motion";

const geistSans = Inter({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Roboto_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        id="page-container"
        className={`${geistSans.variable} ${geistMono.variable} antialiased transition-colors duration-1000 bg-black text-white relative overflow-hidden`}
        style={{ margin: 0, padding: 0 }}
      >
        <ReduxProvider store={store}>
          <WebSocketProvider>
           

            {/* Aurora Overlay */}
            <div className="aurora-overlay pointer-events-none absolute inset-0 z-[-1]" />

            <main>
              <PageAnimatePresence>{children}</PageAnimatePresence>
            </main>
          </WebSocketProvider>
        </ReduxProvider>

        {/* Global Aurora & Sparkle Style */}
        <style jsx global>{`
          .aurora-overlay {
            background: linear-gradient(
              120deg,
              #00f0ff,
              #8affff,
              #d08bff,
              #ff70a6,
              #00f0ff
            );
            background-size: 300% 300%;
            animation: aurora-flow 15s ease-in-out infinite;
            mix-blend-mode: screen;
            opacity: 0.15;
          }

          @keyframes aurora-flow {
            0% {
              background-position: 0% 50%;
            }
            50% {
              background-position: 100% 50%;
            }
            100% {
              background-position: 0% 50%;
            }
          }
        `}</style>
      </body>
    </html>
  );
}
