# Eaze Android Client

Please find the APK [here](https://github.com/cpetzel/homework-android/blob/master/EazeGiphy.apk?raw=true). You can also run the app from Android Studio. 


Once the app is started, I fetch the latest batch of trending gifs from giphy's api. 
I display them in a standard RecyclerView while center cropping the image. The preview gif image is loaded into the recyclerView and loops. As you Scroll down the grid, you will notice it will keep loading more gifs!

When the user taps on a Gif, it will display the gif in another activity. You should notice that there are beautiful transitions between the Activities. You can also drag the detail Activity up or down to dismiss it. 

If you tap the Search Icon while looking at the trending gifs, it will transition to another Activity specific for searching. 

See it in action..
![](https://github.com/cpetzel/homework-android/blob/master/gif.gif "title")



A list of Third party tools used to acheive this...
* [OkHttp3](https://github.com/square/okhttp) for performing the network calls. There is a single instance of the client used for both the Giphy endpoint, and downloading images. 
* [Retrofit](https://github.com/square/retrofit) is providing an interface for communicating with the Giphy API. 
* [GSON](https://github.com/google/gson) for parsing the response/mapping from the API.
* [Glide](https://github.com/bumptech/glide) is the only image loading framework that works with animated Gifs. (No support from Android out of the box). 
* [Dagger2](https://google.github.io/dagger/) for dependency injection. 
* [RxJava](https://github.com/ReactiveX/RxJava) for managing streams of data and asynchronous operations.
* [ButterKnife](http://jakewharton.github.io/butterknife/) for binding views.
* [Stetho](http://facebook.github.io/stetho/) for sniffing/verifying network traffic. 

### A few notes about the project... 

- The Activities are a bit heavy... Ideally I would use a pattern like MVP to abstract some of the heavy loading logic out of them. 
- I used RxJava to help facilitate the loading of data, and the onClickListeners. Managing operations with streams is way easier with RxJava compared with trying to manage concurrency yourself. You simply dispose of the stream when your UI goes away!
- I reused a few UI niceties from an open source project called [Plaid](https://github.com/nickbutcher/plaid)


### Known issues..

* I really wanted to use the Activity Transitions, but sometimes the Gifs don't want to animate correctly. sometimes when you pop back to the calling Activity, the Gif blinks for a split second when the transition is over. This is Glide reloading/restarting the GifDrawable. I didn't spend much time into figuring this out. 
* Handling rotation is hacked. I am simply causing the layouts to resize themselves. Ideally, I would let the Activity tear down and readjust the layouts with the correct resources (grid sizes), and then reload (cached) data. 


## If I had more time...

- Fix the Transitions
- Consider using [Conductor](https://github.com/bluelinelabs/Conductor) with MVP in one Activity, instead of multiple Activities. 
- Unit tests and Robolectric tests. 
- UI Tests (using tools like Mockito for mocking things, and Spoon for running them on multiple devices with screenshots)
- Consider using a staggered grid (centerCropping the gifs is not ideal)
- Put more fun information in the GifDetailActivity
- Other UI Niceties (status bar coloring using Palette toolkit, grid animations, content transitions, Fabs)

