# Shifts Solver
Find a solution to fitting employees in a shifts.

## Feedback

* solving
   * no solution (n/a everybody on 24.12) > report day for which I don't have employees/sport/...
   * preferred day: when looking for who to use, chose from wanted first (keep others for backtrack)

* shifts:
   * morning editor (1x)
   * morning 6am (1x) ... 3 rows in preferences
   * morning 7am (1x)
   * morning 8am (2x)
   * morning sport(1x)

   * afternoon editor (1x)
   * a (4x)
   * a sport(1x)

   * night (1x) ... anybody except sport employee (OK: normal, editor, MorningSportak)

 * weekend shift
   * morning+afternoon editor (1x) *
   * morning 6am (1x)
   * morning sport(1x)

   * morning+afternoon editor (1x) *
   * a (1x)
   * a sport(1x)

   * night (1x) ... anybody except sport employee (OK: normal, editor, MorningSportak)

* employee definition:
   * add MorningSportak
* holidays tab (set of days)
* preferences definition: ...
   * highlight weekends
   * 3 states of preferences: WANT green, default white, NO red > 3 state
     button that changes color by value (mod: 0/1/2)
   * 3 states N/A OK, WANT, IMPOSSIBLE
* solution
   * morning: morning6, morning7, morning8

* desired
   * normal night anybody
   * sunday night is fulltime employee
   * sunday night is editor (not required)
   * friday night is part time employee
   * saturday night is part time employee
   * same team (editor+employees) saturday + sunday (required)
      * editor to have friday afternoon  
   * if saturday+sunday THEN not Monday
   * at least 8 hours between shifts (required)
   * at most one shift / day
   * if night shift, cannot have morning
   * if afternoon shift, morning 8 is first you may have
   * fulltime: max 2 night shifts + morning/afternoon (50%/50%)
   * fulltime MUST get enough shifts (+/-1 shift):
      * workdays * 8 / 7.5 e.g. 21*8/7.5 > nr. of shifts to get (round)
   * IF shift in 5 consecutive days THEN, one day FREE (recommended; if broke solution) ... almost good solution list 

## Plan
Implementation plan:

* Purely client side CRUD for Employees
* ... for Preferences
* ... for Solutions - RD_ONLY calendar for now
* Solver incorporation via buttons on right panels
* Server side persistence
