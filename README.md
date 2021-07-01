#   Work-timer-2
app with interval timers that you can configure by yourself
apk file you can find in `app/release`
app supports english and russian languages

# Screenshots
<img src="https://github.com/ytowka/work-timer-2/blob/main/screenshots/list.png" width="30%" height="30%"> <img src="https://github.com/ytowka/work-timer-2/blob/main/screenshots/timer.png" width="30%" height="30%"> <img src="https://github.com/ytowka/work-timer-2/blob/main/screenshots/fast%20set%20editing.png" width="30%" height="30%">

#   Technology stack:
- MVVM
- Hilt DI
- Navigation Component
- Room
- Coroutines

# Project structure:
there is five main folders in project:
- `adapters` contains all recycler view's adapters
- `app` contains files that i need only for app startup and don't use them frequently
- `utils` contains utility files that can be easily used in other project
- `screen` contains folers wirh fragments and their viewmodels
- `data` contains all files responsible for data sources

# Prehistory:
The idea of interval timer app i have back in 2020 and in august i made first version of work timer. If visuals of the app was not bad, meanwhile code was awful and now i decided to remake that app with my new experience and technologies.


