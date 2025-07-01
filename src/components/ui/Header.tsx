interface HeaderProps {
  title: string
  subtitle?: string
}

export const Header = ({ title, subtitle }: HeaderProps) => {
  return (
    <header className="bg-card shadow-sm border-b border-border">
      <div className="container py-8">
        <div className="text-center">
          <h1 className="text-4xl font-bold text-card-foreground mb-2">
            {title}
          </h1>
          {subtitle && (
            <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
              {subtitle}
            </p>
          )}
        </div>
      </div>
    </header>
  )
}