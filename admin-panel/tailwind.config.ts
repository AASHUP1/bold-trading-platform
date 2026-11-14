import type { Config } from 'tailwindcss';

const config: Config = {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        bg: {
          app: '#0B0F14',
          light: '#F8FAFC',
        },
        surface: {
          card: '#121923',
          elevated: '#1C2734',
          light: '#FFFFFF',
        },
        border: {
          subtle: '#253140',
          light: '#CBD5E1',
        },
        primary: {
          bullish: '#38D996',
          accent: '#2A85FF',
          dark: '#28B87E',
        },
        accent: {
          bearish: '#FF657A',
          warning: '#F3C969',
        },
        text: {
          primary: '#EAF0F6',
          secondary: '#92A0B2',
          muted: '#5A687A',
        },
      },
      fontFamily: {
        sans: ['Inter', '-apple-system', 'BlinkMacSystemFont', 'sans-serif'],
        mono: ['JetBrains Mono', 'Roboto Mono', 'monospace'],
      },
      boxShadow: {
        'glow-green': '0 0 12px rgba(56, 217, 150, 0.25)',
        'glow-red': '0 0 14px rgba(255, 101, 122, 0.35)',
      },
      borderRadius: {
        sm: '6px',
        md: '8px',
        lg: '12px',
        xl: '16px',
      },
    },
  },
  plugins: [],
};

export default config;
