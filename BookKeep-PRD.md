# BookKeep - Product Requirements Document (PRD)

## 1. Product Overview

### 1.1 Product Description
BookKeep is a personal library management system that helps users track their book collection, reading progress, and maintain a comprehensive history of their reading activities. The application supports both owned books and wishlist items, with state-based reading tracking and comprehensive history logging.

### 1.2 Target Users
- Individual book enthusiasts and avid readers
- Personal library collectors
- Users who want to track reading progress and maintain reading history
- People who want to organize their books into custom categories
- People living together and wanting to track who owns which books and where they are

### 1.3 Core Value Proposition
- Comprehensive book metadata management with genre, author, publication year, and page count
- State-based reading progress tracking (Not Started, In Progress, Finished)
- Detailed history logging of all book interactions
- Flexible organization through custom shelves

## 2. Functional Requirements

### 2.1 Book Management

#### 2.1.1 Book Types
- **OwnedBook**: Physical or digital books in the user's possession
  - Required fields: Title, Author, Publication Year, Page Count, Genre, Format (Physical/Digital)
  - Optional fields: Current page number, reading state, history
- **WishlistBook**: Books the user wants to acquire (planned feature, currently limited implementation)

#### 2.1.2 Book Operations
- **Add New Book**: Create books using BookBuilder pattern with validation
- **View Book Details**: Display all book metadata and current status
- **Search and Filter**: 
  - Search by title, author, publication year, genre
  - Filter by year intervals
  - Browse by author or publication year
- **Update Book Information**: Modify book metadata through UI

### 2.2 Reading State Management

#### 2.2.1 State Types
1. **NotStartedState**: Initial state for newly added books
   - Allowed actions: Start reading (transitions to InProgress)
   - Restricted actions: Cannot add comments, quotes, or update page numbers
   
2. **InProgressState**: Active reading state
   - Allowed actions: Update page numbers, add comments, add quotes, finish reading
   - Page tracking: Incremental page updates with validation
   - Comments: Add reading thoughts and notes
   - Quotes: Capture memorable passages with page references
   
3. **FinishedState**: Completed reading state
   - Allowed actions: Add afterthoughts (comments), add quotes, write reviews
   - Restricted actions: Cannot update page numbers or change state further
   - Reviews: Rate books (1-5 stars) with written reviews

#### 2.2.2 State Transitions
- NotStarted → InProgress: User starts reading
- InProgress → Finished: User completes the book
- No backward transitions (one-way progression)

### 2.3 History and Event Tracking

#### 2.3.1 Event Types
- **STARTED_READING**: Timestamp when reading begins
- **FINISHED_READING**: Timestamp when reading completes  
- **COMMENT**: Reading thoughts during InProgress state
- **AFTERTHOUGHT**: Comments added after finishing
- **QUOTE**: Memorable passages with page numbers
- **REVIEW**: Final rating and review (only for finished books)

#### 2.3.2 History Features
- Chronological event logging with timestamps
- Reading duration calculation (start to finish time)
- Event categorization and filtering
- Persistent storage of all interactions

### 2.4 Organization and Shelves

#### 2.4.1 Shelf Management
- **Create Shelves**: Custom categorization (e.g., "Science Fiction", "To Read", "Favorites")
- **Add Books to Shelves**: Books can belong to multiple shelves
- **Remove Books from Shelves**: Shelf membership management
- **Delete Shelves**: Remove categories while preserving books
- **View Shelf Contents**: Browse books by shelf

#### 2.4.2 Library Organization
- Main library contains all books
- Shelves provide additional categorization layers
- UUID-based book identification for reliable references
- Search across all books or within specific shelves

### 2.5 Data Persistence

#### 2.5.1 Current Implementation
- Java serialization to .ser files
- Save/Load library state manually through UI
- Serialized storage in `serializedlibrary/` directory

#### 2.5.2 Planned Enhancements
- JSON-based persistence for better portability
- Automatic saving on changes
- Database integration (SQL/Firebase) for advanced features
- Export/import capabilities

## 3. User Interface Requirements

The current UI isnt great, and wont be used for reference

## 4. Technical Requirements

### 4.1 Architecture Patterns

#### 4.1.1 Design Patterns Used
- **State Pattern**: Reading state management with delegated behavior
- **Builder Pattern**: Book creation with fluent API
- **Abstract Factory**: Book type hierarchy (Book → OwnedBook/WishlistBook)
- **Repository Pattern**: BookStorage for data access abstraction

#### 4.1.2 Class Structure
- **Models**: Core domain objects (Book, OwnedBook, ReadingState subclasses)
- **Collections**: Storage and organization (BookStorage, BookShelf)
- **History**: Event tracking system (BookEvent, BookHistory)
- **Persistence**: Serialization utilities (LibrarySerializer)
- **UI**: Separate terminal and JavaFX interfaces

### 4.1.3 Data Flow
1. BookBuilder creates validated Book instances
2. BookStorage manages all books with UUID-based indexing
3. ReadingState objects handle state-specific behavior delegation
4. BookHistory tracks all interactions with event logging
5. LibrarySerializer handles persistence operations

### 4.2 Technology Stack
- **Java 21**: Core application runtime
- **JavaFX**: GUI framework for primary interface
- **JUnit 5.7**: Testing framework
- **Maven**: Build and dependency management
- **Java Serialization**: Current persistence mechanism

### 4.3 Performance Requirements
- Support for libraries with thousands of books
- Real-time search and filtering responses
- Minimal memory footprint for book metadata
- Fast state transitions and UI updates

## 5. User Experience Requirements

### 5.1 Usability Principles
- **Intuitive Navigation**: Clear menu structures and logical flow
- **State Awareness**: UI adapts to book reading states
- **Error Prevention**: Input validation and confirmation dialogs
- **Consistency**: Uniform interaction patterns across interfaces

### 5.2 Accessibility
- Keyboard navigation support in JavaFX interface
- Clear visual feedback for user actions
- Error messages with actionable guidance
- Terminal interface for screen readers

### 5.3 User Workflows

#### 5.3.1 Adding a New Book
1. Navigate to Add Book tab/menu
2. Fill required fields (title, author, year, pages, genre)
3. Select format (physical/digital)
4. Optionally assign to shelf (existing or new)
5. Confirm creation and return to library view

#### 5.3.2 Reading Progress Tracking
1. Select book from library
2. Start reading (state transition to InProgress)
3. Update page progress as reading progresses
4. Add comments and quotes during reading
5. Finish reading when complete
6. Add final review and rating

#### 5.3.3 Library Organization
1. Create themed shelves (genres, reading lists, etc.)
2. Add books to appropriate shelves
3. Browse books by shelf or search across entire library
4. Manage shelf contents as reading preferences change

## 6. Business Requirements

### 6.1 Success Criteria
- Users can successfully track 100+ books without performance issues
- 90% of user interactions complete without errors
- Reading history data preserved across sessions
- Library state recoverable through save/load operations

### 6.2 Future Enhancements
- **Multi-user Support**: Shared libraries and reading groups
- **Statistics Dashboard**: Reading analytics and progress tracking
- **Recommendation Engine**: AI-powered reading suggestions
- **Web Interface**: Browser-based access for multi-device usage
- **Mobile Apps**: iOS/Android companions for portable access
- **Social Features**: Reading sharing and community features
- **Advanced Search**: Full-text search within notes and quotes
- **Data Export**: PDF reports and reading statistics

### 6.3 Integration Possibilities
- **Goodreads API**: Import existing library data
- **ISBN Database**: Automatic book metadata population
- **E-reader Integration**: Sync reading progress from devices
- **Book Purchase APIs**: Direct purchasing from wishlist
- **Reading Goal Tracking**: Annual reading challenges
- **Book Recommendation Services**: Integration with book discovery platforms

## 7. Constraints and Assumptions

### 7.1 Technical Constraints
- Java-based implementation limits platform portability
- Java serialization creates vendor lock-in for data format
- Single-user design without concurrent access support
- Local storage only (no cloud synchronization)

### 7.2 User Assumptions
- Users have basic computer literacy for GUI interaction
- Users prefer local data storage over cloud-based solutions
- Primary use case is personal library management (not sharing)
- Users are willing to manually input book data

### 7.3 Data Assumptions
- Book metadata relatively stable after creation
- Reading progress generally moves forward (no regression tracking)
- Users read one book at a time per book record
- Comments and quotes are personal notes (not public sharing)

## 8. Validation and Testing

### 8.1 Testing Strategy
- **Unit Tests**: Core domain logic (BookBuilder, State transitions)
- **Integration Tests**: UI components and data persistence
- **User Acceptance Testing**: End-to-end workflows
- **Performance Testing**: Large library simulation

### 8.2 Key Test Scenarios
- Book creation with various metadata combinations
- State transition validation and restriction enforcement
- Shelf management operations
- Save/load data integrity
- Search and filtering accuracy
- Error handling and user feedback

This PRD provides a comprehensive overview of the BookKeep application's expected behavior and functionality based on the existing Java implementation in the @bookkeep-old/ directory.