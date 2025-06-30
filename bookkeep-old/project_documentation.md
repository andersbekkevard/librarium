# Dokumentasjonen kan være i form av en PDF- eller en Markdown-fil (.md) og skal inneholde følgende deler

1. Beskrivelse av appen
2. Diagram
3. Spørsmål

### Beskrivelse av appen

Appen Librarium er en personlig bibliotekstracker som holder styr på bøkene du eier og statusen deres.
Hver bok har en rekke datafelter, som forfatter, sideantall, sjanger osv. Det finnnes flere typer bøker,
man har OwnedBook (bøker man eier) og WishlistBook (bøker man ønsker seg) som begge forlenger den abstrakte
klassen Book. Appen håndterer også status på lesingen av en bok. Hver bok har altså et tilstandsobjekt som sier
hvilken tilstand boken er i (begynt å lese, ferdig å lese etc). Hver gang en metode kalles på en bok, for eksempel
increment page number, så kalles den indre tilstandsklassen i boken for å håndtere kallet slik at det kan løses
på riktig måte (kan eksempelvis ikke lese i bøker som er not started). Boken har også et historikk-objekt som lager
informasjon om hendelser, altså når man begynte å lese etc. Historikken består av BookEvent-objekter. Bøkene lagres i
et bibliotek. Et bibliotek kan ytterligere deles ned i bokhyller, slik at man kan sortere bøkene sine på eksempelvis sjanger.

### Diagram

Se diagram

### Spørsmål

I denne delen skal dere reflektere rundt egne valg, og vise forståelsen dere har for
objektorientert programmering og pensum i emnet. Dere skal besvare følgende spørsmål:

1. Hvilke deler av pensum i emnet dekkes i prosjektet, og på hvilken måte? (For
eksempel bruk av arv, interface, delegering osv.)

Prosjektet har ekstensiv bruk av arv. Ettersom det er forskjellige metoder man har lov å kalle på en bok man eier og en bok man bare ønsker seg, var det naturlig å dele opp i to separate objekter for om man eier eller ønsker seg en bok (en ønsket bok har fek pris, men kan ikke ha format (fysisk/digital)). Men ettersom alle bøker deler generiske felt som forfatter og tittel var det en god løsning å basere seg på den abstrakte klassen Book også heller utvide denne.

Tilstandsløsningen er et klassisk eksempel på bruk av delegeringsteknikker. Jeg delegerer alt av logikken for å operere boken som er avhengig av tilstanden den er i til et eget objekt. Dette er svært nyttig fordi det fjerner mye kode fra bokklassen og gjør det mye lettere å ha oversikt over den. Skulle jeg gjort det på en annen måte måtte jeg ha brukt en rekke switcher eller if-checker, og det ville vært mye logikk i bokklassen som hadde vært vanskelig å ha oversikt over.
BookBuilder-klassen er et annet eksempel på delegering. Dette handler mest om at det er knotete å skulle initialisere ved hjelp av konstruktører, ettersøm det er mange parametere som går inn i en bok. Denne logikken kunne vært bakt rett i en eventuell menyklasse eller en controller, men det å ha det i en egen bookbuilder lar meg bytte ut logikken hvis jeg ønsker å endre måten bøkene lages på, i tillegg til at jeg enkelt kan teste funksjonaliteteten med noen få linjer i mine mainmetoder for feilsøking.

2. Dersom deler av pensum ikke er dekket i prosjektet deres, hvordan kunne dere brukt
disse delene av pensum i appen?

Interface kunne gjerne vært brukt i tilstandsklassene. Dette ville vært naturlig fordi jeg trenger en kontrakt på hva hver enkelt tilstandsobjekt skal kunne gjøre, så jeg alltid er i stand til å kalle metoden i bokklassen. Jeg landet alikevel på å bruke en abstrakt klasse her også, ettersom en tilstand aldri vil ha behov for å arve fra noen andre typer objekter, så da kunne jeg like gjerne bruke opp arven og få abstrahert litt mer kode.

3. Hvordan forholder koden deres seg til Model-View-Controller-prinsippet? (Merk: det
er ikke nødvendig at koden er helt perfekt i forhold til Model-View-Controller
standarder. Det er mulig (og bra) å reflektere rundt svakheter i egen kode)

Alt av logikk for selve objektene bøker og deres lagring etc håndteres av egne klasser, og fxmlapplikasjonen blander seg ikke opp i det. Dette reflekteres i at jeg fra tidligere har laget en terminal-UI som fungerer helt fint, frittstående og uavhengig av FXML-UIen. Dette er jo selve hensikten med Model View Controller, at man skal kunne separere de ulike delene og mixe og matche hvis man ønsker å bytte ut en del for eksempel. View blir da fxml og scenen den bygger, mens controlleren håndterer alt av inputs og interagering med bokklassene.

4. Hvordan har dere gått frem når dere skulle teste appen deres, og hvorfor har dere
valgt de testene dere har? Har dere testet alle deler av koden? Hvis ikke, hvordan
har dere prioritert hvilke deler som testes og ikke? (Her er tanken at dere skal
reflektere rundt egen bruk av tester)

Den første testen jeg skrev var en JUnit test. Jeg har hatt litt trøbbel med å få JUnit til å fungere, så det å ha en egen test jeg kan kjøre for å demonstrere at maven har gitt meg riktig versjon og at alt av testing skal fungere som det skal var svært nyttig. Videre har jeg skrevet tester for BookBuilderklassen. Den var det greit å ha kontroll på, ettersom den samhandler med svært mange andre deler av programmet. Dersom en feil dukker opp her vil det kunne forplante seg til andre tester, og det er derfor svært nyttig å få kontroll på denne klassen så man har oversikt. I tillegg har jeg skrevet tester for en del andre klasser, som historien og events, UI, og også slevsagt for selve bøkene. Jeg har også en egen test for tilstandsobjektet ettersom det er lettere å feilsøke noe dersom skopet er lite

Krav: 500-800 ord
