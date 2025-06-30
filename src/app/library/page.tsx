'use client'

import React, { useState } from 'react'
import { useAuth, useBooks } from '@/hooks'
import { BookList } from '@/components/book'
import { Button } from '@/components/ui/Button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card'
import { Badge } from '@/components/ui/Badge'
import { BookState, BookFilters } from '@/types'
import { Library, Plus, Filter } from 'lucide-react'

const LibraryPage = () => {
  const { user, loading: authLoading } = useAuth()
  const [filters, setFilters] = useState<BookFilters>({})
  const { books, loading: booksLoading, operations } = useBooks(user?.uid || '', filters)

  const handleStateChange = async (bookId: string, newState: BookState) => {
    try {
      const book = books.find(b => b.id === bookId)
      if (book) {
        // Note: In a full implementation, we'd use a more sophisticated state management
        // For now, this demonstrates the pattern
        console.log(`Changing book ${bookId} from ${book.state} to ${newState}`)
      }
    } catch (error) {
      console.error('Failed to update book state:', error)
    }
  }

  const handleProgressUpdate = async (bookId: string, progress: number) => {
    try {
      await operations.updateBook(bookId, { progress })
    } catch (error) {
      console.error('Failed to update progress:', error)
    }
  }

  const filterByState = (state: BookState | undefined) => {
    setFilters(prev => ({ ...prev, state }))
  }

  const filterByOwnership = (isOwned: boolean | undefined) => {
    setFilters(prev => ({ ...prev, isOwned }))
  }

  const clearFilters = () => {
    setFilters({})
  }

  const getBookCounts = () => {
    const total = books.length
    const owned = books.filter(b => b.isOwned).length
    const wishlist = books.filter(b => !b.isOwned).length
    const reading = books.filter(b => b.state === 'in_progress').length
    const finished = books.filter(b => b.state === 'finished').length

    return { total, owned, wishlist, reading, finished }
  }

  if (authLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">Loading...</p>
        </div>
      </div>
    )
  }

  if (!user) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Card className="w-96">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Library className="h-5 w-5" />
              Access Required
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-muted-foreground mb-4">
              Please sign in to access your library.
            </p>
            <Button className="w-full">
              Sign In
            </Button>
          </CardContent>
        </Card>
      </div>
    )
  }

  const counts = getBookCounts()

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-8">
        <div className="flex items-center gap-3">
          <Library className="h-8 w-8" />
          <div>
            <h1 className="text-3xl font-bold">My Library</h1>
            <p className="text-muted-foreground">
              Welcome back, {user.displayName || user.email}
            </p>
          </div>
        </div>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Add Book
        </Button>
      </div>

      {/* Statistics */}
      <div className="grid grid-cols-2 md:grid-cols-5 gap-4 mb-8">
        <Card>
          <CardContent className="p-4 text-center">
            <div className="text-2xl font-bold">{counts.total}</div>
            <div className="text-sm text-muted-foreground">Total Books</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-center">
            <div className="text-2xl font-bold">{counts.owned}</div>
            <div className="text-sm text-muted-foreground">Owned</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-center">
            <div className="text-2xl font-bold">{counts.wishlist}</div>
            <div className="text-sm text-muted-foreground">Wishlist</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-center">
            <div className="text-2xl font-bold">{counts.reading}</div>
            <div className="text-sm text-muted-foreground">Reading</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-center">
            <div className="text-2xl font-bold">{counts.finished}</div>
            <div className="text-sm text-muted-foreground">Finished</div>
          </CardContent>
        </Card>
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-2 mb-6">
        <Button
          variant={!filters.state ? "default" : "outline"}
          size="sm"
          onClick={() => filterByState(undefined)}
        >
          All Books
        </Button>
        <Button
          variant={filters.state === 'not_started' ? "default" : "outline"}
          size="sm"
          onClick={() => filterByState('not_started')}
        >
          Not Started
        </Button>
        <Button
          variant={filters.state === 'in_progress' ? "default" : "outline"}
          size="sm"
          onClick={() => filterByState('in_progress')}
        >
          Currently Reading
        </Button>
        <Button
          variant={filters.state === 'finished' ? "default" : "outline"}
          size="sm"
          onClick={() => filterByState('finished')}
        >
          Finished
        </Button>
        <div className="border-l mx-2" />
        <Button
          variant={filters.isOwned === true ? "default" : "outline"}
          size="sm"
          onClick={() => filterByOwnership(true)}
        >
          Owned
        </Button>
        <Button
          variant={filters.isOwned === false ? "default" : "outline"}
          size="sm"
          onClick={() => filterByOwnership(false)}
        >
          Wishlist
        </Button>
        {(filters.state || filters.isOwned !== undefined) && (
          <Button
            variant="ghost"
            size="sm"
            onClick={clearFilters}
          >
            Clear Filters
          </Button>
        )}
      </div>

      {/* Books */}
      <BookList
        books={books}
        loading={booksLoading}
        onStateChange={handleStateChange}
        onProgressUpdate={handleProgressUpdate}
        emptyMessage={
          Object.keys(filters).length > 0 
            ? "No books match your current filters"
            : "Your library is empty"
        }
      />
    </div>
  )
}

export default LibraryPage