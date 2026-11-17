import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Casino Platform — Admin Operations Portal',
  description: 'Enterprise administration, monitoring, player management, and game telemetry',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body className="min-h-screen bg-[#0B0E14] text-gray-100 flex flex-col">{children}</body>
    </html>
  );
}
