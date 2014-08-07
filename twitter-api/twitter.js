// builds the url of current view to be used for link shortening
function setUrl() {
    var filter = $("input:radio[name='filter']:checked").val(),
        orgsOnly = $("input:checkbox[name='orgs_only']:checked").val(),
        action = "twitter.php?orgs_only=" + orgsOnly + "&filter=" + filter + "&setc=true&" + $("#colSelectForm").serialize();
    $("#filterSelectForm").attr("action", action);
}

$(document).ready(function() { 

    setUrl();

    //generates shortcut URL via bitly API and attaches url to social sharing buttons
    $("#shareButton").click(function () {
        var long_url ="http://jsocialmedia.com/" + $("#filterSelectForm").attr("action");
        long_url = encodeURIComponent(long_url);

        $("#sharePanel").css("display", "block");
        
        $.ajax({
            url: "src/ajax-get-short-url.php",
            data: "long_url=" + long_url,
            dataType: "html",      
            success: function (data)
            {
                $("#share_url").val(data);
                $("a.twitter-share-button").attr("href", "https://twitter.com/intent/tweet?text=" + encodeURIComponent("Check out this amazing info on #Jewishsocialmedia use") + "&url=" + encodeURIComponent(data));
                $("a.facebook-share-button").attr("href", "https://www.facebook.com/sharer/sharer.php?u=" + encodeURIComponent(data));
            },
            error: function () {
                alert("error generating a shortcut url");
            }
        }); 
    });

    
    $("#orgsOnly, #filters, #colSelector input:checkbox").click(function () {
        setUrl();
    });

    // clear filters
    $("#clear_filters").click(function () {
        $("#orgsOnly").attr("checked", false);
        $("input:radio[name='filter']").attr("checked", false);
    });

    // filter list
    $("#filterButton").click(function () {
        $("#filterSelectForm").submit();
    });

    // show/hide columns
    $("#colSelector input:checkbox:not(:checked)").each(function () {
        var column = "table ." + $(this).attr("name");
        $(column).hide();
    });

    // toggle which columns are shown/hidden
    $("#colSelector input:checkbox").click(function () {

        var column = "table ." + $(this).attr("name");

        //hide display while we rearrange, makes page load faster
        $("table #myTable").attr("display", "none");

        //name of column shows/hides class in table
        if ($("#colSelector input:checkbox").is(":checked")) {
            $(column).css("display", "table-cell");
        } else {
            $(column).css("display", "none");
        }

        $("#colSelector input:checkbox:not(:checked)").each(function () {
            var column = "table ." + $(this).attr("name");
            $(column).hide();
        });

        //redisplay
        $("table #myTable").attr("display", "block");
    });
    
    // display column select gui
    $("#showColsButton").click(function () {
        $("#colSelector").css("display", "block");
        $("#sharePanel").css("display", "none");
    });

    // display url share gui
    $("#shareButton").click(function () {
        $("#colSelector").hide();
        $("#sharePanel").show();
    });

    $(".close-icon").click(function () {
        $(this).parent().hide();
    });

});