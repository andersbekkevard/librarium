import type { Metadata } from 'next'
import { Inter } from 'next/font/google'
import './globals.css'
import { ThemeProvider } from '@/contexts/theme-context'

const inter = Inter({ subsets: ['latin'] })

export const metadata: Metadata = {
  title: 'Librarium - Personal Book Library',
  description: 'A modern web application for personal book library management built with Firebase-native patterns and TypeScript-first development.',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className={inter.className}>
        <ThemeProvider defaultTheme="system" storageKey="librarium-ui-theme">
          {children}
        </ThemeProvider>
      </body>
    </html>
  )
}