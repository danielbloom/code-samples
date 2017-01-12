code-samples
============
Some brief samples of some of my work in **PHP**, **Java**, **jQuery**, **Backbone.js** and **MySQL**.
I have limited ability to post proprietary code. Please note that files have been edited.

##Java
This is an Restlet API resource that provides patient data in json form to a React.js front end module. It retrieves data from the db using queryDSL, does a lot of post-processing, before converting the data to the format used by the front end. It uses Junit and Mockito for tests.
View [Java code sample](https://github.com/danielbloom/code-samples/tree/master/java-Restlet-API)

##PHP and jQuery (plus MVC and grunt)
I have limited ability to post propriatary code, but here is an partial example from a page in a web app where a user grants or revokes permission for the app to integrate with third party services. The front end uses jQuery and Grunt for task automation and asset management. Both the front and back end use versions of the Zend MVC structure.
View [dir](https://github.com/danielbloom/code-samples/tree/master/integrations)
View sample [JavaScript](https://github.com/danielbloom/code-samples/blob/master/integrations/integrations.js)
View all files as [single feature](https://github.com/danielbloom/code-samples/commit/ff93ffe42494c2aab6eaf3506389615de1792915)

##Backbone.js
I worked on a Backbone.js application which acted as a dashboard for call center managers. It asynchronously displays the status of all call center workers who are constantly changing status from Open/Closed/On Call etc. A dummy example can be viewed [here](https://secure.ifbyphone.com/managerpanel.php?key=adc1bbaf0f133450c6eccd4b55a134284cdbfae9&usr_queue_id=31494) though production cases may have hundreds of agents active on the dashboard. The features that I primarily contributed are the ability to sort the agents by call priority, or by name alphabetically or reverse alphabetically. These features are demonstrated in this [tutorial](http://vimeo.com/86921727#t=1m08s). It is important to note that this was a legacy codebase using an older version of Backbone and as such does not 100% comply with current Backbone design practices.
Code can be viewed [here](https://github.com/danielbloom/code-samples/tree/master/managerpanel).

##jQuery and Twitter API
Years ago I developed a custom social media analytics platform utilizing the Twitter API, PHP and jQuery. It can be accessed at http://jsocialmedia.com - username:password is daniel:daniel - and the [video](https://www.youtube.com/watch?v=BPCLizmQlf8) can walk you through the technology. The code itself is a few years old and is not up to my current standards, but I've included some of the [jQuery](https://github.com/danielbloom/code-samples/tree/master/twitter-api) that handles the show/hide of columns and the retrieval of the shortcut url via the Bitly API. If I did today this kind of data and interaction would be better handled using Backbone.js or Angular.

## Some other features I built
[Agent Panel Persistance](http://vimeo.com/85774075)

[After Call Work mode](http://vimeo.com/82999970) This was a super tricky pull request with amost twenty files changed including changes to telephony controller logic, the Backbone.js manager panel and a heap of client and server side changes.

