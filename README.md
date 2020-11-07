# MusicPlayerMDM
A very simple music player

## ToDo List
- [X] Evitare che cliccando nella lista canzoni il player si sinterrompa
- [ ] Fare interfaccia base per utenti non esperti, anziani, bambini e interfaccia avanzata
- [ ] Finestra di "Playlist" --> implementare?
- [ ] Fare finestre di conferma per ogni azione (alcune le ho già fatte by Lau)
- [ ] Modificare ampiezza programma: full screen e personalizzato
- [ ] Visualizzazione copertina album
- [ ] Fare tutorial da far partire al primo avvio dell'applicazione

### Bottoni menubar da implementare:
- [ ] Open file
- [ ] Next song
- [ ] Previous song
- [ ] Volume +
- [ ] Volume -
- [ ] Volume muto
- [ ] Full screen mode
- [ ] Minimize windows
- [ ] Interface Simple
- [ ] Interface Advanced
- [ ] How to use
- [ ] Hotkeys

_________________________________________

## AFTER MEETING 04/11

Modificare menu FILE come segue:

1) "Open file..." 			
	- diventa "Add file..."
	- implementare come append

2) "Import files from folder..."
	- diventa "Add files from folder..."
	- modificare in modo che faccia append e non sostituisci

3) "Close" 
	- diventa "Exit"
	- già implementato, basta cambiare solo nome del menuitem

4) Aggiungere voce "Clear the playlist" (Chiudi la scaletta) + implementare
5) Aggiungere voce "Remove selected file" (Rimuovi il file selezionato) + implementare

---

Modificare menu RIPRODUZIONE come segue:

6) "Mute" 
	- diventa "Mute/Unmute"
	- implementarlo per fare in modo che se volume=0, lo mette a metà. Se volume!=0, lo muta

7) "Increase Volume" e "Decrease Volume "
	- Modificare le frecce degli shortcut
	- Implementare aumentando/diminuendo il volume di +/-10

--- 

8) Valutare se fare FullScreen o meno

9) Se non esistono metadati del file, mettere nome file nella colonna "titolo"

10) Realizzare interfaccia base seguendo design visto insieme con Paint

______________________________________________________

## Done List
- [X] Correggere errore riproduzione automatica
- [X] Fare menu in alto con File, Help, etc
- [X] Tradurre in un'altra lingua, che sia inglese o italiano o altro
- [X] Aggiungere info quando con il mouse vai sopra ai bottoni
- [X] Fare partire la canzone con un doppio click 
- [X] Aggiungere il pausa/play se premi la barra spaziatrice/Invio/tasto P
- [X] Aggiungere 4/5 shortcut
