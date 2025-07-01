'use client'

import * as React from 'react'
import { useState } from 'react'
import { 
  BarChart3, 
  BookOpen, 
  Home, 
  Library, 
  Menu, 
  Plus, 
  Settings, 
  Target, 
  Users, 
  ClipboardList,
  HousePlus
} from 'lucide-react'

import { Button } from '@/components/ui/button'
import { ThemeToggle } from '@/components/ui/theme-toggle'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { 
  Sidebar, 
  SidebarHeader, 
  SidebarContent, 
  SidebarNav, 
  SidebarNavItem, 
  SidebarFooter 
} from '@/components/ui/sidebar'
import { Sheet, SheetContent, SheetTrigger } from '@/components/ui/sheet'

interface AppLayoutProps {
  children: React.ReactNode
  currentPage?: string
}

const navigationItems = [
  {
    title: 'Dashboard',
    href: '/dashboard',
    icon: Home,
    id: 'dashboard'
  },
  {
    title: 'My Library',
    href: '/library',
    icon: Library,
    id: 'library'
  },
  {
    title: 'Add Books',
    href: '/add-books',
    icon: Plus,
    id: 'add-books'
  },
  {
    title: 'Statistics',
    href: '/statistics',
    icon: BarChart3,
    id: 'statistics'
  },
  {
    title: 'Goals',
    href: '/goals',
    icon: Target,
    id: 'goals'
  },
  {
    title: 'Reading Log',
    href: '/reading-log',
    icon: ClipboardList,
    id: 'reading-log'
  },
  {
    title: 'Collaboration',
    href: '/collaboration',
    icon: Users,
    id: 'collaboration'
  },
  {
    title: 'Household',
    href: '/household',
    icon: HousePlus,
    id: 'household'
  },
]

const SidebarContentComponent = ({ currentPage, onNavigate }: { 
  currentPage?: string
  onNavigate: (page: string) => void 
}) => (
  <>
    <SidebarHeader>
      <div className="flex items-center gap-2">
        <BookOpen className="h-6 w-6" />
        <span className="font-semibold text-lg">Librarium</span>
      </div>
    </SidebarHeader>
    
    <SidebarContent>
      <SidebarNav>
        {navigationItems.map((item) => {
          const Icon = item.icon
          return (
            <SidebarNavItem
              key={item.id}
              active={currentPage === item.id}
              onClick={() => onNavigate(item.id)}
              style={{ cursor: 'pointer' }}
            >
              <Icon className="h-4 w-4" />
              {item.title}
            </SidebarNavItem>
          )
        })}
      </SidebarNav>
    </SidebarContent>
    
    <SidebarFooter>
      <div className="flex items-center gap-3">
        <Avatar className="h-8 w-8">
          <AvatarImage src="/placeholder-avatar.jpg" />
          <AvatarFallback>JD</AvatarFallback>
        </Avatar>
        <div className="flex-1 min-w-0">
          <p className="text-sm font-medium truncate">John Doe</p>
          <p className="text-xs text-muted-foreground truncate">john@example.com</p>
        </div>
        <Button variant="ghost" size="icon" className="h-8 w-8">
          <Settings className="h-4 w-4" />
        </Button>
      </div>
    </SidebarFooter>
  </>
)

export const AppLayout = ({ children, currentPage = 'dashboard' }: AppLayoutProps) => {
  const [activePage, setActivePage] = useState(currentPage)
  const [sidebarOpen, setSidebarOpen] = useState(false)

  const handleNavigate = (page: string) => {
    setActivePage(page)
    setSidebarOpen(false) // Close mobile sidebar on navigation
  }

  return (
    <div className="flex h-screen bg-background">
      {/* Desktop Sidebar */}
      <div className="hidden lg:flex">
        <Sidebar>
          <SidebarContentComponent currentPage={activePage} onNavigate={handleNavigate} />
        </Sidebar>
      </div>

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Top Header */}
        <header className="border-b bg-card px-4 py-3 lg:px-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              {/* Mobile Menu Button */}
              <Sheet open={sidebarOpen} onOpenChange={setSidebarOpen}>
                <SheetTrigger asChild>
                  <Button variant="ghost" size="icon" className="lg:hidden">
                    <Menu className="h-5 w-5" />
                  </Button>
                </SheetTrigger>
                <SheetContent side="left" className="w-64 p-0">
                  <Sidebar className="border-0">
                    <SidebarContentComponent currentPage={activePage} onNavigate={handleNavigate} />
                  </Sidebar>
                </SheetContent>
              </Sheet>

              {/* Page Title */}
              <h1 className="text-xl font-semibold">
                {navigationItems.find(item => item.id === activePage)?.title || 'Dashboard'}
              </h1>
            </div>

            {/* Right Side Actions */}
            <div className="flex items-center gap-2">
              <ThemeToggle />
              
              {/* Desktop User Menu */}
              <div className="hidden lg:flex items-center gap-3 ml-4 pl-4 border-l">
                <Avatar className="h-8 w-8">
                  <AvatarImage src="/placeholder-avatar.jpg" />
                  <AvatarFallback>JD</AvatarFallback>
                </Avatar>
                <div className="hidden xl:block">
                  <p className="text-sm font-medium">John Doe</p>
                  <p className="text-xs text-muted-foreground">john@example.com</p>
                </div>
              </div>
            </div>
          </div>
        </header>

        {/* Main Content */}
        <main className="flex-1 overflow-auto">
          {children}
        </main>
      </div>
    </div>
  )
}