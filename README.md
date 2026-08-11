# Word-finder-v2 (This is slightly better than V4, and is also in JAVA)
Application that acts as a middle man for finding words. Can be opened anywhere and fully customisable.
*Note that this code is a modified version of [WORD-FINDER-V4](https://github.com/KnifeEater/WORD-FINDER-V4) that I made... in JAVA*  
  
  
  **Modes**  
  ***Normal search***
  Calculates normal frequency based on the given wordlist. Uses that frequency for ranks.
  *Pros:* Frequency is always the same, no matter how many searches you do.
  *Cons:* Some already "discarded" words may cloud your frequency list.
  Alright to use if the word is everyday-used word.

  ***Perfect search***
  Calculates normal frequency based on the candidates wordlist. That wordlist collects all "could be" words.
  *Pros:* Frequency changes dynamically after every new input, making it more accurate. Generally faster than its counterpart.
  *Cons:* It MAY take one or two more searches than necessary. Really depends on the word structure
  Great for more rarer words.

  ***Binary search***
  Extension of perfect search!
  Calculates normal frequency based on the candidates wordlist. That wordlist collects all "could be" words.
  Looks at the median word score (like a bell curve). Algorithm is based on binary search for lists.
  *Pros:* Same pros as perfect search. May be a little slower, but still faster than normal mode. Eleminates letters faster
  *Cons:* Incredibly mediocre in the late game. Use only at the beginning/middle of your search journey!
  Great for faster word elemination, not for lookup.
  
  ## IMPORT dictionary and frequency!  
  You can now inport your library of choice :)
  (Not 100% sure if it works for Unicode characters, but who knows, may be my next update) 
  *Only restriction is that it doesn't accept characters in alphabet made out of two letters or more. Keep that in mind...*  
  So, how do you do it? *Easy.*  
  Just select import options and type a path to the files and BOOM, it overwrites its dictionary/frequency!  
  **All done. You can now enjoy your modded word finder!**
  
  
    
### Notes:  
**When finding a 10+ char length word, be sure to indicate it with letters in place and _'s.**  
**GUI might glitch if you change the settings fast enough. That will not affect your searches.**  
      
      
      
**SOURCES:**
Finally, without a word libary, this code wouldn't be here.  
Please, check out [dwyl](https://github.com/dwyl) if you are interested in projects like these!  
  
  
**Please enjoy my code, and thank you for reading this!**
