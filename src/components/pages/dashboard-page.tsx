'use client'

import * as React from 'react'
import { Book, Calendar, Target, TrendingUp, Clock, Award } from 'lucide-react'

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Progress } from '@/components/ui/progress'
import { Button } from '@/components/ui/button'
import { BookCard } from '@/components/book/BookCard'
import { mockDashboardData, enhancedMockBooks } from '@/lib/enhanced-mock-data'

const StatCard = ({ 
  title, 
  value, 
  description, 
  icon: Icon, 
  trend 
}: {
  title: string
  value: string | number
  description: string
  icon: React.ElementType
  trend?: string
}) => (
  <Card>
    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
      <CardTitle className="text-sm font-medium">{title}</CardTitle>
      <Icon className="h-4 w-4 text-muted-foreground" />
    </CardHeader>
    <CardContent>
      <div className="text-2xl font-bold">{value}</div>
      <p className="text-xs text-muted-foreground">
        {trend && <span className="text-green-600">{trend}</span>} {description}
      </p>
    </CardContent>
  </Card>
)

const ActivityItem = ({ 
  event, 
  book 
}: { 
  event: {
    id: string
    type: string
    timestamp: string | Date
    data: {
      stateChange?: { to: string }
      progressUpdate?: { pagesRead?: number }
      rating?: { rating: number }
    }
  }
  book: { title: string } | undefined
}) => {
  const getEventDescription = () => {
    switch (event.type) {
      case 'state_change':
        return `Marked "${book?.title}" as ${event.data.stateChange?.to.replace('_', ' ')}`
      case 'progress_update':
        return `Read ${event.data.progressUpdate?.pagesRead || 0} pages in "${book?.title}"`
      case 'review':
        return `Reviewed "${book?.title}"`
      case 'quote':
        return `Added a quote from "${book?.title}"`
      case 'rating':
        return `Rated "${book?.title}" ${event.data.rating?.rating} stars`
      default:
        return `Updated "${book?.title}"`
    }
  }

  const getEventIcon = () => {
    switch (event.type) {
      case 'state_change':
        return '📖'
      case 'progress_update':
        return '📊'
      case 'review':
        return '✍️'
      case 'quote':
        return '💭'
      case 'rating':
        return '⭐'
      default:
        return '📚'
    }
  }

  return (
    <div className="flex items-start gap-3 p-3 rounded-lg hover:bg-accent/50 transition-colors">
      <div className="text-lg">{getEventIcon()}</div>
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium">{getEventDescription()}</p>
        <p className="text-xs text-muted-foreground">
          {new Date(event.timestamp).toLocaleDateString()} at{' '}
          {new Date(event.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
        </p>
      </div>
    </div>
  )
}

const GoalCard = ({ goal }: { goal: {
  id: string
  title: string
  description?: string
  status: string
  currentValue: number
  targetValue: number
  targetType: string
  endDate: string | Date
} }) => {
  const progress = (goal.currentValue / goal.targetValue) * 100
  
  return (
    <Card>
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <CardTitle className="text-sm font-medium">{goal.title}</CardTitle>
          <Badge variant={goal.status === 'active' ? 'default' : 'secondary'}>
            {goal.status}
          </Badge>
        </div>
        {goal.description && (
          <CardDescription className="text-xs">{goal.description}</CardDescription>
        )}
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          <div className="flex items-center justify-between text-sm">
            <span>{goal.currentValue} / {goal.targetValue} {goal.targetType}</span>
            <span className="font-medium">{Math.round(progress)}%</span>
          </div>
          <Progress value={progress} className="h-2" />
          <p className="text-xs text-muted-foreground">
            Due {new Date(goal.endDate).toLocaleDateString()}
          </p>
        </div>
      </CardContent>
    </Card>
  )
}

export const DashboardPage = () => {
  const { quickStats, recentActivity, currentlyReading, recentlyFinished, upcomingGoals } = mockDashboardData

  return (
    <div className="p-6 space-y-6">
      {/* Welcome Section */}
      <div className="space-y-2">
        <h1 className="text-3xl font-bold tracking-tight">Welcome back, John!</h1>
        <p className="text-muted-foreground">
          Here&apos;s what&apos;s happening with your reading journey today.
        </p>
      </div>

      {/* Quick Stats */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="Books This Month"
          value={quickStats.booksThisMonth}
          description="books completed"
          icon={Book}
          trend="+2"
        />
        <StatCard
          title="Pages This Week"
          value={quickStats.pagesThisWeek}
          description="pages read"
          icon={TrendingUp}
          trend="+12%"
        />
        <StatCard
          title="Current Streak"
          value={`${quickStats.currentStreak} days`}
          description="reading streak"
          icon={Calendar}
        />
        <StatCard
          title="Completion Rate"
          value={`${quickStats.completionRate}%`}
          description="of started books finished"
          icon={Target}
        />
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        {/* Currently Reading */}
        <div className="lg:col-span-2 space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold">Currently Reading</h2>
            <Button variant="outline" size="sm">
              View All
            </Button>
          </div>
          
          {currentlyReading.length > 0 ? (
            <div className="grid gap-4 sm:grid-cols-2">
              {currentlyReading.map((book) => (
                <BookCard key={book.id} book={book} />
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="flex items-center justify-center py-12">
                <div className="text-center space-y-2">
                  <Book className="h-12 w-12 mx-auto text-muted-foreground" />
                  <p className="text-muted-foreground">No books currently being read</p>
                  <Button size="sm">Start Reading</Button>
                </div>
              </CardContent>
            </Card>
          )}

          {/* Recent Activity */}
          <div className="mt-8">
            <h2 className="text-xl font-semibold mb-4">Recent Activity</h2>
            <Card>
              <CardContent className="p-0">
                {recentActivity.length > 0 ? (
                  <div className="divide-y">
                    {recentActivity.map((event) => {
                      const book = enhancedMockBooks.find(b => b.id === event.bookId)
                      return (
                        <ActivityItem key={event.id} event={event} book={book} />
                      )
                    })}
                  </div>
                ) : (
                  <div className="p-8 text-center text-muted-foreground">
                    No recent activity
                  </div>
                )}
              </CardContent>
            </Card>
          </div>
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {/* Reading Goals */}
          <div>
            <h2 className="text-xl font-semibold mb-4">Active Goals</h2>
            <div className="space-y-3">
              {upcomingGoals.map((goal) => (
                <GoalCard key={goal.id} goal={goal} />
              ))}
            </div>
          </div>

          {/* Recently Finished */}
          <div>
            <h2 className="text-xl font-semibold mb-4">Recently Finished</h2>
            {recentlyFinished.length > 0 ? (
              <div className="space-y-3">
                {recentlyFinished.map((book) => (
                  <Card key={book.id}>
                    <CardContent className="p-4">
                      <div className="flex items-center gap-3">
                        <div className="h-12 w-8 bg-muted rounded flex-shrink-0 flex items-center justify-center">
                          📚
                        </div>
                        <div className="flex-1 min-w-0">
                          <p className="font-medium text-sm truncate">{book.title}</p>
                          <p className="text-xs text-muted-foreground truncate">{book.author}</p>
                          <div className="flex items-center gap-1 mt-1">
                            <span className="text-xs">⭐</span>
                            <span className="text-xs">{book.metadata.rating}/5</span>
                          </div>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            ) : (
              <Card>
                <CardContent className="p-6 text-center">
                  <Award className="h-8 w-8 mx-auto text-muted-foreground mb-2" />
                  <p className="text-sm text-muted-foreground">No recently finished books</p>
                </CardContent>
              </Card>
            )}
          </div>

          {/* Quick Actions */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Quick Actions</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <Button className="w-full justify-start" variant="outline">
                <Book className="h-4 w-4 mr-2" />
                Add New Book
              </Button>
              <Button className="w-full justify-start" variant="outline">
                <Clock className="h-4 w-4 mr-2" />
                Log Reading Session
              </Button>
              <Button className="w-full justify-start" variant="outline">
                <Target className="h-4 w-4 mr-2" />
                Set New Goal
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}