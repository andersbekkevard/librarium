'use client'

import * as React from 'react'
import { useState, useMemo } from 'react'
import { 
  Search, 
  Filter, 
  Grid3X3, 
  List, 
  Table,
  Plus,
  MoreHorizontal,
  SortAsc,
  SortDesc,
  BookOpen
} from 'lucide-react'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { BookCard } from '@/components/book/BookCard'
import { BookList } from '@/components/book/BookList'
import { enhancedMockBooks, mockShelves } from '@/lib/enhanced-mock-data'
import type { Book, BookState } from '@/types'

type ViewMode = 'grid' | 'list' | 'table'
type SortField = 'title' | 'author' | 'createdAt' | 'updatedAt' | 'rating'
type SortOrder = 'asc' | 'desc'

const LibraryFilters = ({ 
  searchQuery, 
  onSearchChange, 
  selectedStates, 
  onStateChange,
  selectedGenres,
  onGenreChange 
}: {
  searchQuery: string
  onSearchChange: (query: string) => void
  selectedStates: BookState[]
  onStateChange: (states: BookState[]) => void
  selectedGenres: string[]
  onGenreChange: (genres: string[]) => void
}) => {
  const allGenres = Array.from(
    new Set(enhancedMockBooks.flatMap(book => book.metadata.genre || []))
  ).sort()

  const states = [
    { value: 'not_started' as BookState, label: 'Not Started' },
    { value: 'in_progress' as BookState, label: 'In Progress' },
    { value: 'finished' as BookState, label: 'Finished' }
  ]

  const toggleState = (state: BookState) => {
    if (selectedStates.includes(state)) {
      onStateChange(selectedStates.filter(s => s !== state))
    } else {
      onStateChange([...selectedStates, state])
    }
  }

  const toggleGenre = (genre: string) => {
    if (selectedGenres.includes(genre)) {
      onGenreChange(selectedGenres.filter(g => g !== genre))
    } else {
      onGenreChange([...selectedGenres, genre])
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg flex items-center gap-2">
          <Filter className="h-5 w-5" />
          Filters
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Search by title, author, or ISBN..."
            value={searchQuery}
            onChange={(e) => onSearchChange(e.target.value)}
            className="pl-10"
          />
        </div>

        {/* Reading States */}
        <div>
          <label className="text-sm font-medium mb-2 block">Reading State</label>
          <div className="flex flex-wrap gap-2">
            {states.map((state) => (
              <Button
                key={state.value}
                variant={selectedStates.includes(state.value) ? "default" : "outline"}
                size="sm"
                onClick={() => toggleState(state.value)}
              >
                {state.label}
              </Button>
            ))}
          </div>
        </div>

        {/* Genres */}
        <div>
          <label className="text-sm font-medium mb-2 block">Genres</label>
          <div className="flex flex-wrap gap-2 max-h-32 overflow-y-auto">
            {allGenres.slice(0, 12).map((genre) => (
              <Button
                key={genre}
                variant={selectedGenres.includes(genre) ? "default" : "outline"}
                size="sm"
                onClick={() => toggleGenre(genre)}
              >
                {genre}
              </Button>
            ))}
          </div>
          {allGenres.length > 12 && (
            <p className="text-xs text-muted-foreground mt-1">
              And {allGenres.length - 12} more...
            </p>
          )}
        </div>

        {/* Clear Filters */}
        {(searchQuery || selectedStates.length > 0 || selectedGenres.length > 0) && (
          <Button 
            variant="ghost" 
            size="sm" 
            onClick={() => {
              onSearchChange('')
              onStateChange([])
              onGenreChange([])
            }}
          >
            Clear All Filters
          </Button>
        )}
      </CardContent>
    </Card>
  )
}

const LibraryStats = ({ books }: { books: Book[] }) => {
  const stats = useMemo(() => {
    const owned = books.filter(book => book.isOwned)
    const wishlist = books.filter(book => !book.isOwned)
    const finished = owned.filter(book => book.state === 'finished')
    const inProgress = owned.filter(book => book.state === 'in_progress')
    const totalPages = finished.reduce((sum, book) => sum + (book.metadata.pages || 0), 0)

    return {
      total: books.length,
      owned: owned.length,
      wishlist: wishlist.length,
      finished: finished.length,
      inProgress: inProgress.length,
      totalPages
    }
  }, [books])

  return (
    <div className="grid grid-cols-2 md:grid-cols-6 gap-4">
      <div className="text-center">
        <div className="text-2xl font-bold text-primary">{stats.total}</div>
        <div className="text-xs text-muted-foreground">Total</div>
      </div>
      <div className="text-center">
        <div className="text-2xl font-bold text-green-600">{stats.owned}</div>
        <div className="text-xs text-muted-foreground">Owned</div>
      </div>
      <div className="text-center">
        <div className="text-2xl font-bold text-purple-600">{stats.wishlist}</div>
        <div className="text-xs text-muted-foreground">Wishlist</div>
      </div>
      <div className="text-center">
        <div className="text-2xl font-bold text-blue-600">{stats.finished}</div>
        <div className="text-xs text-muted-foreground">Finished</div>
      </div>
      <div className="text-center">
        <div className="text-2xl font-bold text-yellow-600">{stats.inProgress}</div>
        <div className="text-xs text-muted-foreground">Reading</div>
      </div>
      <div className="text-center">
        <div className="text-2xl font-bold text-indigo-600">{stats.totalPages.toLocaleString()}</div>
        <div className="text-xs text-muted-foreground">Pages Read</div>
      </div>
    </div>
  )
}

const BookTable = ({ books }: { books: Book[] }) => {
  return (
    <div className="rounded-md border">
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead className="border-b bg-muted/50">
            <tr>
              <th className="px-4 py-3 text-left text-sm font-medium">Title</th>
              <th className="px-4 py-3 text-left text-sm font-medium">Author</th>
              <th className="px-4 py-3 text-left text-sm font-medium">Status</th>
              <th className="px-4 py-3 text-left text-sm font-medium">Progress</th>
              <th className="px-4 py-3 text-left text-sm font-medium">Rating</th>
              <th className="px-4 py-3 text-left text-sm font-medium">Actions</th>
            </tr>
          </thead>
          <tbody>
            {books.map((book) => (
              <tr key={book.id} className="border-b hover:bg-muted/50">
                <td className="px-4 py-3">
                  <div>
                    <div className="font-medium">{book.title}</div>
                    <div className="text-sm text-muted-foreground">
                      {book.metadata.pages} pages • {book.metadata.publishedYear}
                    </div>
                  </div>
                </td>
                <td className="px-4 py-3 text-sm">{book.author}</td>
                <td className="px-4 py-3">
                  <Badge variant={
                    book.state === 'finished' ? 'default' :
                    book.state === 'in_progress' ? 'secondary' : 'outline'
                  }>
                    {book.state.replace('_', ' ')}
                  </Badge>
                </td>
                <td className="px-4 py-3">
                  {book.state === 'in_progress' && book.progress && book.metadata.pages ? (
                    <div className="w-24">
                      <div className="text-xs mb-1">
                        {Math.round((book.progress / book.metadata.pages) * 100)}%
                      </div>
                      <div className="w-full bg-gray-200 rounded-full h-2">
                        <div 
                          className="bg-primary h-2 rounded-full" 
                          style={{ width: `${(book.progress / book.metadata.pages) * 100}%` }}
                        />
                      </div>
                    </div>
                  ) : (
                    <span className="text-sm text-muted-foreground">—</span>
                  )}
                </td>
                <td className="px-4 py-3">
                  {book.metadata.rating ? (
                    <div className="flex items-center gap-1">
                      <span>⭐</span>
                      <span className="text-sm">{book.metadata.rating}</span>
                    </div>
                  ) : (
                    <span className="text-sm text-muted-foreground">—</span>
                  )}
                </td>
                <td className="px-4 py-3">
                  <Button variant="ghost" size="sm">
                    <MoreHorizontal className="h-4 w-4" />
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export const LibraryPage = () => {
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedStates, setSelectedStates] = useState<BookState[]>([])
  const [selectedGenres, setSelectedGenres] = useState<string[]>([])
  const [viewMode, setViewMode] = useState<ViewMode>('grid')
  const [sortField, setSortField] = useState<SortField>('title')
  const [sortOrder, setSortOrder] = useState<SortOrder>('asc')
  const [activeShelf, setActiveShelf] = useState('all')

  const filteredBooks = useMemo(() => {
    let filtered = enhancedMockBooks

    // Search filter
    if (searchQuery) {
      const query = searchQuery.toLowerCase()
      filtered = filtered.filter(book => 
        book.title.toLowerCase().includes(query) ||
        book.author.toLowerCase().includes(query) ||
        book.isbn?.toLowerCase().includes(query) ||
        book.metadata.genre?.some(genre => genre.toLowerCase().includes(query))
      )
    }

    // State filter
    if (selectedStates.length > 0) {
      filtered = filtered.filter(book => selectedStates.includes(book.state))
    }

    // Genre filter
    if (selectedGenres.length > 0) {
      filtered = filtered.filter(book => 
        book.metadata.genre?.some(genre => selectedGenres.includes(genre))
      )
    }

    // Shelf filter
    if (activeShelf !== 'all') {
      const shelf = mockShelves.find(s => s.id === activeShelf)
      if (shelf) {
        filtered = filtered.filter(book => shelf.bookIds.includes(book.id))
      }
    }

    // Sort
    filtered.sort((a, b) => {
      let aVal: string | number | Date
      let bVal: string | number | Date

      switch (sortField) {
        case 'title':
          aVal = a.title
          bVal = b.title
          break
        case 'author':
          aVal = a.author
          bVal = b.author
          break
        case 'createdAt':
          aVal = new Date(a.createdAt)
          bVal = new Date(b.createdAt)
          break
        case 'updatedAt':
          aVal = new Date(a.updatedAt)
          bVal = new Date(b.updatedAt)
          break
        case 'rating':
          aVal = a.metadata.rating || 0
          bVal = b.metadata.rating || 0
          break
        default:
          aVal = a.title
          bVal = b.title
      }

      if (aVal < bVal) return sortOrder === 'asc' ? -1 : 1
      if (aVal > bVal) return sortOrder === 'asc' ? 1 : -1
      return 0
    })

    return filtered
  }, [searchQuery, selectedStates, selectedGenres, activeShelf, sortField, sortOrder])

  const toggleSort = (field: SortField) => {
    if (sortField === field) {
      setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc')
    } else {
      setSortField(field)
      setSortOrder('asc')
    }
  }

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">My Library</h1>
          <p className="text-muted-foreground">
            Manage your book collection, track reading progress, and organize your library.
          </p>
        </div>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Add Book
        </Button>
      </div>

      {/* Stats */}
      <LibraryStats books={filteredBooks} />

      {/* Shelves and View Controls */}
      <div className="flex flex-col lg:flex-row gap-6">
        {/* Shelves */}
        <div className="lg:w-1/4">
          <LibraryFilters 
            searchQuery={searchQuery}
            onSearchChange={setSearchQuery}
            selectedStates={selectedStates}
            onStateChange={setSelectedStates}
            selectedGenres={selectedGenres}
            onGenreChange={setSelectedGenres}
          />

          {/* Shelves */}
          <Card className="mt-6">
            <CardHeader>
              <CardTitle className="text-lg">Shelves</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                <Button
                  variant={activeShelf === 'all' ? 'default' : 'ghost'}
                  className="w-full justify-start"
                  onClick={() => setActiveShelf('all')}
                >
                  <BookOpen className="h-4 w-4 mr-2" />
                  All Books ({enhancedMockBooks.length})
                </Button>
                {mockShelves.map((shelf) => (
                  <Button
                    key={shelf.id}
                    variant={activeShelf === shelf.id ? 'default' : 'ghost'}
                    className="w-full justify-start"
                    onClick={() => setActiveShelf(shelf.id)}
                  >
                    <div 
                      className="w-3 h-3 rounded-full mr-2"
                      style={{ backgroundColor: shelf.color }}
                    />
                    {shelf.name} ({shelf.bookIds.length})
                  </Button>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Main Content */}
        <div className="lg:w-3/4 space-y-4">
          {/* View Controls */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Button
                variant={viewMode === 'grid' ? 'default' : 'outline'}
                size="sm"
                onClick={() => setViewMode('grid')}
              >
                <Grid3X3 className="h-4 w-4" />
              </Button>
              <Button
                variant={viewMode === 'list' ? 'default' : 'outline'}
                size="sm"
                onClick={() => setViewMode('list')}
              >
                <List className="h-4 w-4" />
              </Button>
              <Button
                variant={viewMode === 'table' ? 'default' : 'outline'}
                size="sm"
                onClick={() => setViewMode('table')}
              >
                <Table className="h-4 w-4" />
              </Button>
            </div>

            <div className="flex items-center gap-2">
              <Button
                variant="outline"
                size="sm"
                onClick={() => toggleSort('title')}
                className="flex items-center gap-1"
              >
                Title
                {sortField === 'title' && (
                  sortOrder === 'asc' ? <SortAsc className="h-3 w-3" /> : <SortDesc className="h-3 w-3" />
                )}
              </Button>
              <Button
                variant="outline"
                size="sm"
                onClick={() => toggleSort('author')}
                className="flex items-center gap-1"
              >
                Author
                {sortField === 'author' && (
                  sortOrder === 'asc' ? <SortAsc className="h-3 w-3" /> : <SortDesc className="h-3 w-3" />
                )}
              </Button>
              <Button
                variant="outline"
                size="sm"
                onClick={() => toggleSort('updatedAt')}
                className="flex items-center gap-1"
              >
                Updated
                {sortField === 'updatedAt' && (
                  sortOrder === 'asc' ? <SortAsc className="h-3 w-3" /> : <SortDesc className="h-3 w-3" />
                )}
              </Button>
            </div>
          </div>

          {/* Books Display */}
          {filteredBooks.length === 0 ? (
            <Card>
              <CardContent className="flex items-center justify-center py-12">
                <div className="text-center space-y-2">
                  <BookOpen className="h-12 w-12 mx-auto text-muted-foreground" />
                  <p className="text-muted-foreground">No books found</p>
                  <p className="text-sm text-muted-foreground">
                    Try adjusting your filters or add some books to your library.
                  </p>
                </div>
              </CardContent>
            </Card>
          ) : (
            <>
              {viewMode === 'grid' && (
                <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                  {filteredBooks.map((book) => (
                    <BookCard key={book.id} book={book} />
                  ))}
                </div>
              )}
              
              {viewMode === 'list' && (
                <BookList books={filteredBooks} />
              )}
              
              {viewMode === 'table' && (
                <BookTable books={filteredBooks} />
              )}
            </>
          )}
        </div>
      </div>
    </div>
  )
}