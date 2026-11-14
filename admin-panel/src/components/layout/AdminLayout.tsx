import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { Header } from './Header';

export const AdminLayout: React.FC = () => {
  const [mobileNavOpen, setMobileNavOpen] = useState(false);

  return (
    <div className="flex h-screen w-screen overflow-hidden bg-bg-app">
      <Sidebar isOpen={mobileNavOpen} onClose={() => setMobileNavOpen(false)} />
      <div className="flex flex-col flex-1 min-w-0 overflow-hidden">
        <Header onToggleNav={() => setMobileNavOpen(prev => !prev)} />
        <main className="flex-1 overflow-y-auto p-4 sm:p-6 md:p-8 bg-bg-app">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
