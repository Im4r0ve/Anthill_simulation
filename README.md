
# Anthill_simulation

The app can be launched using the following Maven command:
```
mvn run:javafx
```

## Documentation:
Documentation is available after downloading the repo and opening index.html in javadoc folder

## Changing default values:
Default values can be changed in the class App.java in the function generateSettings()

## Using your own map:
If you want to use your own map or modify the default one you need to put it
into resources folder. Then you need to update this line in the function loadImage() in App.java
```
defaultImage = new Image("file:resources/yourMapName.png");
```