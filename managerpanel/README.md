##Manager Panel featuring Backbone.js, jQuery, Underscore
I worked on a Backbone.js application which acted as a dashboard for call center managers. It asynchronously displays the status of all call center workers who are constantly changing status from Open/Closed/On Call etc. An dummy example can be viewed [here](https://secure.ifbyphone.com/managerpanel.php?key=adc1bbaf0f133450c6eccd4b55a134284cdbfae9&usr_queue_id=31494) though production cases may have hundreds of agents active on the dashboard. 

The features that I primarily contributed are the ability to sort the agents by call priority, or by name alphabetically or reverse alphabetically. These features are demonstrated in this [tutorial](http://vimeo.com/86921727#t=1m08s). It is important to note that this was a legacy codebase using an older version of Backbone with legacy design patterns and as such does not 100% comply with current Backbone design practices.

