/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        background: '#0B0E14',
        surface: '#121824',
        surfaceVariant: '#1A2234',
        primaryGold: '#F59E0B',
        neonCyan: '#06B6D4',
        neonEmerald: '#10B981',
      },
    },
  },
  plugins: [],
};
