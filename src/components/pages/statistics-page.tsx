'use client'

import * as React from 'react'
import { useMemo } from 'react'
import { 
  BarChart3, 
  BookOpen, 
  Clock, 
  Target,
  TrendingUp,
  Calendar,
  Award,
  Activity,
  PieChart
} from 'lucide-react'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Progress } from '@/components/ui/progress'
import { Badge } from '@/components/ui/badge'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { enhancedMockBooks, mockBookEvents, mockReadingGoals } from '@/lib/enhanced-mock-data'

interface StatCard {
  title: string
  value: string | number
  change?: string
  trend?: 'up' | 'down' | 'neutral'
  icon: React.ComponentType<{ className?: string }>
}

const StatCards = ({ stats }: { stats: StatCard[] }) => (
  <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
    {stats.map((stat, index) => {
      const Icon = stat.icon
      return (
        <Card key={index}>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              {stat.title}
            </CardTitle>
            <Icon className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stat.value}</div>
            {stat.change && (
              <p className={`text-xs flex items-center gap-1 ${
                stat.trend === 'up' ? 'text-green-600' : 
                stat.trend === 'down' ? 'text-red-600' : 
                'text-muted-foreground'
              }`}>
                {stat.trend === 'up' && <TrendingUp className="h-3 w-3" />}
                {stat.change}
              </p>
            )}
          </CardContent>
        </Card>
      )
    })}
  </div>
)

const ReadingProgress = () => {
  const currentYear = new Date().getFullYear()
  const yearlyGoal = mockReadingGoals.find(g => g.targetType === 'books')
  const booksFinishedThisYear = enhancedMockBooks.filter(book => 
    book.finishedAt && new Date(book.finishedAt).getFullYear() === currentYear
  ).length
  
  const progressPercentage = yearlyGoal ? 
    Math.round((booksFinishedThisYear / yearlyGoal.targetValue) * 100) : 0

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Target className="h-5 w-5" />
          {currentYear} Reading Goal
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="space-y-2">
          <div className="flex justify-between text-sm">
            <span>Books Read</span>
            <span>{booksFinishedThisYear} / {yearlyGoal?.targetValue || 0}</span>
          </div>
          <Progress value={progressPercentage} className="h-2" />
          <div className="text-xs text-muted-foreground">
            {progressPercentage}% complete
          </div>
        </div>
        
        {yearlyGoal && (
          <div className="text-sm space-y-1">
            <div className="flex justify-between">
              <span>Days Remaining:</span>
              <span>{Math.ceil((new Date(currentYear, 11, 31).getTime() - Date.now()) / (1000 * 60 * 60 * 24))}</span>
            </div>
            <div className="flex justify-between">
              <span>Books Needed:</span>
              <span>{Math.max(0, yearlyGoal.targetValue - booksFinishedThisYear)}</span>
            </div>
            <div className="flex justify-between">
              <span>Pace:</span>
              <span className={booksFinishedThisYear >= (yearlyGoal.targetValue * (new Date().getMonth() + 1) / 12) ? 'text-green-600' : 'text-orange-600'}>
                {booksFinishedThisYear >= (yearlyGoal.targetValue * (new Date().getMonth() + 1) / 12) ? 'On Track' : 'Behind'}
              </span>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  )
}

const GenreBreakdown = () => {
  const genreStats = useMemo(() => {
    const genreCounts: Record<string, number> = {}
    
    enhancedMockBooks.forEach(book => {
      if (book.state === 'finished' && book.metadata.genre) {
        book.metadata.genre.forEach(genre => {
          genreCounts[genre] = (genreCounts[genre] || 0) + 1
        })
      }
    })

    return Object.entries(genreCounts)
      .sort(([,a], [,b]) => b - a)
      .slice(0, 8)
  }, [])

  const total = genreStats.reduce((sum, [, count]) => sum + count, 0)

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <PieChart className="h-5 w-5" />
          Genre Distribution
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          {genreStats.map(([genre, count]) => (
            <div key={genre} className="space-y-1">
              <div className="flex justify-between text-sm">
                <span>{genre}</span>
                <span>{count} books ({Math.round((count / total) * 100)}%)</span>
              </div>
              <Progress value={(count / total) * 100} className="h-2" />
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  )
}

const MonthlyReadingChart = () => {
  const monthlyData = useMemo(() => {
    const months = [
      'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
      'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
    ]
    
    const currentYear = new Date().getFullYear()
    const monthlyStats = Array(12).fill(0)
    
    enhancedMockBooks.forEach(book => {
      if (book.finishedAt) {
        const finishedDate = new Date(book.finishedAt)
        if (finishedDate.getFullYear() === currentYear) {
          monthlyStats[finishedDate.getMonth()]++
        }
      }
    })

    return months.map((month, index) => ({
      month,
      books: monthlyStats[index]
    }))
  }, [])

  const maxBooks = Math.max(...monthlyData.map(d => d.books))

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <BarChart3 className="h-5 w-5" />
          Monthly Reading Activity
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {monthlyData.map((data) => (
            <div key={data.month} className="flex items-center gap-4">
              <div className="w-8 text-sm text-muted-foreground">
                {data.month}
              </div>
              <div className="flex-1 relative">
                <div className="h-8 bg-muted rounded">
                  <div 
                    className="h-full bg-primary rounded transition-all duration-500"
                    style={{ width: `${maxBooks > 0 ? (data.books / maxBooks) * 100 : 0}%` }}
                  />
                </div>
                <div className="absolute inset-y-0 right-2 flex items-center text-sm font-medium">
                  {data.books}
                </div>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  )
}

const ReadingStreaks = () => {
  const recentActivity = mockBookEvents
    .filter(event => event.type === 'state_change' && event.data.stateChange?.to === 'finished')
    .sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())
    .slice(0, 5)
    .map(event => {
      const book = enhancedMockBooks.find(b => b.id === event.bookId)
      return {
        ...event,
        bookTitle: book?.title || 'Unknown Book'
      }
    })

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Activity className="h-5 w-5" />
          Recent Activity
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          {recentActivity.map((event) => (
            <div key={event.id} className="flex items-center gap-3">
              <div className="flex-shrink-0">
                <div className="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
                  <Award className="h-4 w-4 text-green-600" />
                </div>
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium truncate">
                  Finished reading &ldquo;{event.bookTitle}&rdquo;
                </p>
                <p className="text-xs text-muted-foreground">
                  {new Date(event.timestamp).toLocaleDateString()}
                </p>
              </div>
            </div>
          ))}
        </div>
        
        {recentActivity.length === 0 && (
          <div className="text-center py-6 text-muted-foreground">
            <Activity className="h-8 w-8 mx-auto mb-2 opacity-50" />
            <p className="text-sm">No recent reading activity</p>
          </div>
        )}
      </CardContent>
    </Card>
  )
}

const ReadingTime = () => {
  const averageReadingTime = useMemo(() => {
    const finishedBooks = enhancedMockBooks.filter(book => 
      book.state === 'finished' && book.startedAt && book.finishedAt
    )
    
    if (finishedBooks.length === 0) return 0
    
    const totalDays = finishedBooks.reduce((sum, book) => {
      const start = new Date(book.startedAt!).getTime()
      const end = new Date(book.finishedAt!).getTime()
      return sum + Math.ceil((end - start) / (1000 * 60 * 60 * 24))
    }, 0)
    
    return Math.round(totalDays / finishedBooks.length)
  }, [])

  const currentlyReading = enhancedMockBooks.filter(book => book.state === 'in_progress')

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Clock className="h-5 w-5" />
          Reading Insights
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div className="text-center">
            <div className="text-2xl font-bold text-primary">{averageReadingTime}</div>
            <div className="text-xs text-muted-foreground">Avg Days/Book</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-blue-600">{currentlyReading.length}</div>
            <div className="text-xs text-muted-foreground">Currently Reading</div>
          </div>
        </div>
        
        <div className="space-y-2">
          <h4 className="text-sm font-medium">Books in Progress</h4>
          {currentlyReading.slice(0, 3).map((book) => (
            <div key={book.id} className="flex items-center justify-between text-sm">
              <span className="truncate flex-1">{book.title}</span>
              <Badge variant="secondary" className="ml-2">
                {book.progress && book.metadata.pages ? 
                  `${Math.round((book.progress / book.metadata.pages) * 100)}%` : 
                  'Started'
                }
              </Badge>
            </div>
          ))}
          {currentlyReading.length === 0 && (
            <p className="text-xs text-muted-foreground">No books currently in progress</p>
          )}
        </div>
      </CardContent>
    </Card>
  )
}

export const StatisticsPage = () => {
  const overallStats = useMemo(() => {
    const totalBooks = enhancedMockBooks.length
    const finishedBooks = enhancedMockBooks.filter(book => book.state === 'finished').length
    const inProgressBooks = enhancedMockBooks.filter(book => book.state === 'in_progress').length
    const totalPages = enhancedMockBooks
      .filter(book => book.state === 'finished')
      .reduce((sum, book) => sum + (book.metadata.pages || 0), 0)

    return [
      {
        title: 'Total Books',
        value: totalBooks,
        change: '+2 this month',
        trend: 'up' as const,
        icon: BookOpen
      },
      {
        title: 'Books Finished',
        value: finishedBooks,
        change: '+1 this week',
        trend: 'up' as const,
        icon: Award
      },
      {
        title: 'Currently Reading',
        value: inProgressBooks,
        change: 'Steady pace',
        trend: 'neutral' as const,
        icon: Activity
      },
      {
        title: 'Pages Read',
        value: totalPages.toLocaleString(),
        change: '+156 this week',
        trend: 'up' as const,
        icon: BarChart3
      }
    ]
  }, [])

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Statistics</h1>
          <p className="text-muted-foreground">
            Track your reading progress and discover insights about your reading habits.
          </p>
        </div>
      </div>

      {/* Overview Stats */}
      <StatCards stats={overallStats} />

      {/* Main Content */}
      <Tabs defaultValue="overview" className="space-y-6">
        <TabsList className="grid w-full grid-cols-4">
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="progress">Progress</TabsTrigger>
          <TabsTrigger value="genres">Genres</TabsTrigger>
          <TabsTrigger value="activity">Activity</TabsTrigger>
        </TabsList>

        <TabsContent value="overview" className="space-y-6">
          <div className="grid gap-6 md:grid-cols-2">
            <ReadingProgress />
            <ReadingTime />
          </div>
          <div className="grid gap-6 md:grid-cols-2">
            <MonthlyReadingChart />
            <ReadingStreaks />
          </div>
        </TabsContent>

        <TabsContent value="progress" className="space-y-6">
          <div className="grid gap-6 md:grid-cols-2">
            <ReadingProgress />
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Calendar className="h-5 w-5" />
                  Reading Goals
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {mockReadingGoals.map((goal) => (
                    <div key={goal.id} className="space-y-2">
                      <div className="flex justify-between text-sm">
                        <span className="capitalize">{goal.targetType}</span>
                        <span>{goal.currentValue} / {goal.targetValue}</span>
                      </div>
                      <Progress 
                        value={(goal.currentValue / goal.targetValue) * 100} 
                        className="h-2" 
                      />
                      <div className="text-xs text-muted-foreground">
                        {Math.round((goal.currentValue / goal.targetValue) * 100)}% complete
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>
          <MonthlyReadingChart />
        </TabsContent>

        <TabsContent value="genres" className="space-y-6">
          <div className="grid gap-6 md:grid-cols-2">
            <GenreBreakdown />
            <Card>
              <CardHeader>
                <CardTitle>Favorite Authors</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {Array.from(new Set(enhancedMockBooks.map(book => book.author)))
                    .slice(0, 6)
                    .map((author) => (
                      <div key={author} className="flex items-center justify-between">
                        <span className="text-sm">{author}</span>
                        <Badge variant="secondary">
                          {enhancedMockBooks.filter(book => book.author === author).length} books
                        </Badge>
                      </div>
                    ))}
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        <TabsContent value="activity" className="space-y-6">
          <div className="grid gap-6 md:grid-cols-2">
            <ReadingStreaks />
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <TrendingUp className="h-5 w-5" />
                  Reading Streak
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-center space-y-2">
                  <div className="text-3xl font-bold text-green-600">7</div>
                  <div className="text-sm text-muted-foreground">Day current streak</div>
                  <div className="text-xs text-muted-foreground">
                    Longest streak: 23 days
                  </div>
                </div>
                <div className="mt-4 flex justify-center gap-1">
                  {Array.from({ length: 14 }, (_, i) => (
                    <div
                      key={i}
                      className={`w-3 h-3 rounded-sm ${
                        i < 7 ? 'bg-green-500' : 'bg-gray-200'
                      }`}
                    />
                  ))}
                </div>
                <div className="text-xs text-center text-muted-foreground mt-2">
                  Last 14 days
                </div>
              </CardContent>
            </Card>
          </div>
          <MonthlyReadingChart />
        </TabsContent>
      </Tabs>
    </div>
  )
}