# RxUsa

This Android app uses RxJava in it's architecture. The accompanying slides from the DCAndroid Meetup presentation can be found [here] (https://docs.google.com/presentation/d/1BJIC-iS80oT8qWT7aFaNiUyy8H7sgZwOooB7pJxFqjA/edit?usp=sharing).

The app is modeled on the US Federal Government. The user can 'log in' to the app by ratifying a constitution. The main activity manages screens for the Executive, Legislative, and Judicial branches of government. The branches of government communicate with each other with a common service.

### Potential Improvements

 - _Every branch should have it's own service and persistence layer:_ Right now, the branches get information from a common message service, but that's not how it works in the real world. Branches should store 'Bills' or government artifacts in their own persistence layers and *trade* information. The messaging service probably still needs to exist to avoid cyclic dependencies, but it may not need to store any information.
 - _Add the concept of Time:_ This would open the door to election and voting simulations. There are other time-based functions that could be implemented first, for example, if the Legislature sends a bill to the President and the president doesn't act within 10 days, the bill is automatically signed into law.

### Design Areas:
 - Ideas to make the user-experience more interesting, fun, and interactive are welcome. Feel free to comment and make issues.

### FIXME
- Bottom bar icons need color to reflect their selection state
