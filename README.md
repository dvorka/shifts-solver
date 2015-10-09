# Shifts Solver
Finds a solution to fitting employees with preferences in a schedule for
shifts-based operation.



## Functional Specification
Employee roles:
   * editor
   * staffer
   * morning sportak
   * sportak

Employee jobs:
   * fulltime
   * part time

Shift types:
   * workdays
      * morning shift
         * 1x editor
         * 1x 6AM staffer
         * 1x 7AM staffer
         * 1x 8AM staffer
         * 1x sportak (can be morning sportak)
      * afternoon shift
         * 1x editor
         * 4x staffer
         * 1x sportak
      * night shift
         * 1x staffer (*MUST:* staffer, editor, morning sportak i.e. not sportak)
   * weekend
      * morning shift
         * 1x editor (*MUST:* same as Friday afternoon)
         * 1x 6AM staffer
         * 1x sportak
      * afternoon shift
         * 1x editor (*MUST:* same as morning)
         * 1x staffer
         * 1x sportak
      * night shift
         * 1x staffer (*MUST:* staffer, editor, morning sportak i.e. not sportak)

Schedule:
   * For one month
   * Dependencies to previous month schedule (e.g. editor Friday to Sunday continuity)

Employee preferences:
   * preference type:
      * YES: I want this shift/day
      * NO: I cannot make this shift/day
      * NO-PREFERENCE: I don't care
   * preference target:
      * (whole) day
      * any shift type (see above)

Rules:
   * *MUST:* shift counts for one job (except editor continuity below)
   * *MUST:* any employee role may serve on Monday to Saturday nights
   * *MUST:* part time employee to serve on Friday night
   * *MUST:* part time employee to serve on Saturday night
   * *MUST:* fulltime employee to serve on Sunday night (TODO)
   * *SHOULD:* editor to serve on Sunday night (TODO)
   * *MUST:* employee birthday is marked as NO preference
   * *MUST:* national holidays are solved in the same way Sunday (TODO)
   * Editor Friday to Sunday continuity:
      * *MUST:* particular editor to serve Friday afternoon + Saturday morning
        and afternoon + Sunday morning and afternoon shifts
      * *MUST:* editor continuity counts for 1 + 3 (Friday + weekend) = 4 shifts
        in job (TODO)
      * *SHOULD:* editor to have same staffer and sportak in continuity (TODO)
   * *MUST:* employee cannot have more than one shift in a day (there must be
     at least 8 hours between two shifts served by the same employee)
   * *MUST:* if employee serves afternoon shift, then first shift it may serve
     is morning 8AM (6AM and 7AM CANNOT be served by this employee) (TODO)
   * *MUST:* if employee serves on Saturday and Sunday then it CANNOT serve on Monday (TODO)
   * *MUST:* if employee serves night shift, then it CANNOT serve morning shift
     next day (TODO)
   * *SHOULD:* if employee has any shift in 5 consecutive days, then it SHOULD get
     one day FREE (TODO)
   * Fulltime employee jobs:
      * *MUST:* job shifts = workdays * 8 / 7.5
      * *MUST:* fulltime employee must get max shifts +/-1 shift
      * *MUST:* 2 night shifts in month at most (TODO)
      * *SHOULD:* morning and afternoon shifts to be balanced 50%/50% (TODO)
   * Part time employee jobs:
      * *MUST:* max job shifts = fulltime job shifts / 2



## Technical Architecture
Frontend:
   * Google Web Toolkit

Backend:
   * Google AppEngine



## Plan
The implementation plan:
   * employee preferences:
      * CRUD (edit and load in panels + store to RIA state)
   * solver
      * incorporate NEGATIVE preferences conditions to solver as additional constraints
      * incorporate POSITIVE preferences in solver (do 2 cycles of employees
        and remember it in solution: want employees for day and THEN don't
        care employees)
      * if no solution found, report deepest cause i.e. day where is the problem
        e.g. day 23, afternoon shift, editor role.
      * national holidays panel, CRUD and solver logic
   * server side persistence
   * authentication and authorization

Readme:
   * S2 specification: rules
   * screenshots of main panels
   * link to live demo version

---
