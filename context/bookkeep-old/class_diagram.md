```mermaid
classDiagram
    class ReadingState {
        <<abstract>>
        #OwnedBook book
        +startReading() void
        +stopReading() void
        +changeState() void
  +...()...
    }

    class NotStartedState {
        +startReading() void
        +stopReading() void
        +changeState() void
  +...()...
    }

    class InProgressState {
        +startReading() void
        +stopReading() void
        +changeState() void
  +...()...
    }

    class FinishedState {
        +startReading() void
        +stopReading() void
        +changeState() void
  +...()...
    }

    class Book {
        <<abstract>>
        #String title
        #String authorName
        #int publicationYear
        #int pageCount
        #Genre genre
        #UUID id
        +getHistory() BookHistory
        +addComment(String) void
        +addQuote(String, int) void
        +review(String, int) void
        +incrementPageNumber(int) void
        +getState() ReadingState
        +getStateName() String
        +changeState() void
    }

    class OwnedBook {
        -BookFormat format
        -ReadingState state
        -int pageNumber
        -BookHistory history
        +getState() ReadingState
        +setState(ReadingState) void
        +addComment(comment: String) void
        +addQuote(quote: String, pageNumber: int) void
        +review(reviewText: String, rating: int) void
        +incrementPageNumber(increment: int) void
        +changeState() void
        +getStateName() String
    }

    class BookHistory {
        -List~BookEvent~ listOfEvents
        -BookEvent startedReading
        -BookEvent finishedReading
        -BookEvent review
        +addEvent(BookEvent) void
    }

    class BookEvent {
        -Instant timestamp
        -EventType type
        -String text
        -int pageNumber
        -int rating
    }
    
    Book <|-- OwnedBook
    OwnedBook "1" --> "1" ReadingState : has
    OwnedBook "1" --> "1" BookHistory : has
    ReadingState <|-- NotStartedState
    ReadingState <|-- InProgressState
    ReadingState <|-- FinishedState
    ReadingState --> OwnedBook : references
    BookHistory "1" --> "*" BookEvent : contains
