# One A Day

### What it does

One A Day is a small android app being built along my journey learning Android development.
Its' purpose is to show the user an old photo or image in the phone's storage, one per day.
The user can then decided whether to delete the image or save it, freeing space in the former case.
The goal is to help the user slowly get rid of unwanted photos, while also taking a walk down memory lane.


### Under Development

As I am still in the process of learning more about Android dev, a lot of functionality is incomplete.

* ~~Need to implement a save (do nothing) button and cycle to the next image~~
* ~~Need to provide meaningful user feedback in the case of a deleted image~~
* ~~Fill in the code at L147 in MainActivity~~
* Plan for a more responsive solution to fetching the correct image in the case of having a large resultset
  * Can do this by writing an async query to save the next image's path in Shared Prefs while the user makes a decision on the current image.
