'use client'

import { AppLayout } from '@/components/layout/app-layout'
import { DashboardPage } from '@/components/pages/dashboard-page'

export default function HomePage() {
  return (
    <AppLayout currentPage="dashboard">
      <DashboardPage />
    </AppLayout>
  )
}