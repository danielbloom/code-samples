<?php /**
*
* managerPanel.php
* NOTE: file has been edited for public dsiplay
*
*/
?>

<!DOCTYPE html>
<html>
<head>
  <title>Manager Control Panel - <?php echo XXXXX; ?></title>
  <link rel="stylesheet" href="css/calldist/grid_870_15.css"/>
  <link rel="stylesheet" href="css/calldist/manager_panel.css"/>
  <link rel="stylesheet" href="assets/css/jquery.selectBox.css"/>

  <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
  <script type="text/javascript">CLOSURE_NO_DEPS=true;</script>
  <script type="text/javascript">
      var App = {};
      App.account_id = <?php echo XXXXX; ?>;
      App.distributor_id = <?php echo XXXXX; ?>;
      App.queue_id = <?php echo XXXXX; ?>;
      App.caller_id = <?php echo XXXXX; ?>;
      App.cometUrl = 'https://push.ifbyphone.com/cometd';
      App.cometChannel = <?php echo XXXXX; ?>;
      App.monitoringEnabled = 0;
      App.isReadOnly = 0;
  </script>
  <script src="js/calldist/manager_panel.min.js"></script>
  <script type="text/javascript">
    $(document).ready(function () {
      $('.show_tooltip1').hover(
          function (event) {
              $('#tooltip1').show().css({
                  top: event.pageY + 10,
                  left: event.pageX - 30
              });
          },
          function () {
              $('#tooltip1').hide();
          }
      );

      $('.show_tooltip2').hover(
          function (event) {
              $('#tooltip2').show().css({
                  top: event.pageY + 10,
                  left: event.pageX - 30
              });
          },
          function () {
              $('#tooltip2').hide();
          }
      );

      function resizeAgentList() {
          $('#agent_list').css('height', $(window).innerHeight() - 300);
      }

      $(window).resize(resizeAgentList);
      resizeAgentList();

      // open read only manager panel
      $('#open_read_only').click( function() {
          window.open('managerpanel.php?key=<?php echo XXXXX; ?>&usr_queue_id=<?php echo XXXXX; ?>', 'ReadOnlyPanel31494',
                               'menubar=no,status=no,location=no,toolbar=no,width=885,height=545');
      });

      App.agent_list.reset([<?php echo XXXXX; ?>]);
      App.Timer.trigger('tick');

      // handling of agent filtering on initial page load
      var modes = App.filter.toJSON(),
          filter_agent_modes = ["after_call_work","busy_work","open","outbound","break","busy_away","closed","lunch"],
          set_mode = {};

      if(filter_agent_modes != 'all') {

          $.each(modes, function(mode, show) {
              if ($.inArray(mode, filter_agent_modes) < 0) {
                  // hide mode
                  set_mode[mode] = false;
              }
          });
          App.filter.set(set_mode);
      }

      // hide filter select popup when clicking outside of it
      $(document).mouseup(function (e) {
          var $container = $("#agent_filter"),
              $agent_filter_select = $("#agent_filter_select");

          // if the target of the click isn't the container nor a descendant of the container
          if (!$container.is(e.target)
              && $container.has(e.target).length === 0
              && $container.is(':visible'))
          {
              $container.hide();
          } else if (($agent_filter_select.is(e.target) || $agent_filter_select.has(e.target).length !== 0) && $container.is(':hidden')) {
              $container.show();
          }
      });

  });
  </script>
</head>
<body>

<div class="container_15">

 <div class="grid_9">
    <h1>Manager Control Panel - <?php echo XXXXX; ?></h1>
    <span><img src="images/read-only_manager_panel_icon.png" border="0"/> <a href="#" id="open_read_only">Open the Read-Only Panel</a></span> </div>
<div class="grid_5" id="filter_wrapper">Display Agents:
    <span id="agent_filter_select">
        <span id="agent_filter_status"> All Modes</span><img src="images/arrow-down-black.png" style="vertical-align:middle;">
        <div id="agent_filter"></div>
    </span>
</div>
 <div class="grid_1">
    <h5 id="version">v3.0</h5>
 </div>

 <div class="grid_15 table_box" id="agent_list">
  <div class="agent_list_wrapper">
  <table>
   <thead>
   <tr>
    <th class="agent_order sort_by_queue">#</th>
    <th class="agent_name sort_by_alpha">Agent</th>
    <th class="agent_mode">Mode<span id="modes_filtered"><br>(filtered)</span></th>
    <th class="agent_time">Time In This Mode</th>
    <th class="agent_calls_accepted show_tooltip1">Calls Accepted&#135;</th>
    <th class="agent_agent_denied show_tooltip2">Agent Denied&#135;*</th>
    <th class="agent_denied_other show_tooltip2">Denied Other&#135;*</th>
    <th class="agent_no_answer_or_busy show_tooltip2">No Answer or Busy&#135;*</th>
    <th class="agent_average_talk_time show_tooltip2">Average Talk Time&#135;*</th>
   </tr>
   </thead>
  </table>
  </div>
 </div>

 <div class="grid_4 table_box" id="queue">
  <h2>Currently In Queue</h2>
  <table>
   <thead>
   <tr>
    <th class="queue_ani">Number</th>
    <th class="queue_wait_time">Wait Time</th>
   </tr>
   </thead>
  </table>
  <div class="tbody_wrapper">
   <table>
    <tbody id="queued_calls_table_body">
    </tbody>
   </table>
  </div>
  <table>
   <tfoot>
    <tr>
     <td>Total Calls: <span id="queue_total_calls"></span></td>
     <td><span style="font-weight: bold;">Avg:</span> <span id="queue_average_wait"></span></td>
    </tr>
   </tfoot>
  </table>
 </div>

 <div class="grid_11" id="queue_stats_section">

 <div class="grid_5 table_box alpha" id="results">
  <h2>Call Result Breakdown</h2>
  <table>
   <thead>
   <tr>
    <th>Call Result</th>
    <th>%</th>
    <th>Avg Wait</th>
   </tr>
  </thead>
  <tbody>
  <tr id="results_answered">
		<td>Answered</td>
		<td class="percent"><?php echo XXXXX; ?>%</td>
		<td class="time"><?php echo XXXXX; ?></td>
	</tr>
	<tr id="results_hung_up">
		<td>Hung Up</td>
		<td class="percent"><?php echo XXXXX; ?>%</td>
		<td class="time"><?php echo XXXXX; ?></td>
	</tr>
	<tr id="results_aborted">
		<td>Opted Out</td>
		<td class="percent"><?php echo XXXXX; ?></td>
		<td class="time"><?php echo XXXXX; ?></td>
	</tr>
	<tr id="results_size_exceeded">
		<td>Queue Full</td>
		<td class="percent"><?php echo XXXXX; ?></td>
		<td class="time"><?php echo XXXXX; ?></td>
	</tr>
	<tr id="results_time_exceeded">
		<td>Time Exceeded</td>
		<td class="percent"><?php echo XXXXX; ?></td>
		<td class="time"><?php echo XXXXX; ?></td>
	</tr>
	<tr id="results_agents_closed">
		<td>All Agents Closed</td>
		<td class="percent"><?php echo XXXXX; ?></td>
		<td class="time"><?php echo XXXXX; ?></td>
	</tr>
  <tr id="results_callback_cancelled">
    <td>Callback Canceled</td>
    <td class="percent"><?php echo XXXXX; ?></td>
    <td class="time"><?php echo XXXXX; ?></td>
  </tr>
  <tr id="results_callback_completed">
    <td>Callback Completed</td>
    <td class="percent"><?php echo XXXXX; ?></td>
    <td class="time"><?php echo XXXXX; ?></td>
  </tr>
  </tbody>
  </table>
 </div>

 <div class="grid_6 table_box omega" id="queue_stats">
  <h2 class="show_tooltip1">Queue Statistics&#135;</h2>
  <table>
   <tbody>
   <tr>
    <th>Longest Hold Time</th>
    <td id="longest_hold"></td>
   </tr>
   <tr>
    <th>Average Hold Time</th>
    <td id="average_hold"></td>
   </tr>
   <tr>
    <th>Total Calls Today</th>
    <td id="calls_today"></td>
   </tr>
   <tr>
    <th>Longest Inb. Talk Time</th>
    <td id="longest_incoming_talk"></td>
   </tr>
   <tr>
    <th>Average Inb. Talk Time</th>
    <td id="average_incoming_talk"></td>
   </tr>
   <tr>
    <th>Most Calls in Queue Today</th>
    <td id="most_calls"></td>
   </tr>
   </tbody>
   <tfoot>
    <tr>
     <th>
      &#135;Statistics are updated every minute.<br/>
      *Call then returned to queue
     </th>
     <td>
      <button class="queue_stats_refresh">Refresh</button>
     </td>
   </tfoot>
  </table>
 </div>

 </div>

</div>

<div id="tooltip1">&#135;Statistics are updated every minute.</div>
<div id="tooltip2">&#135;Statistics are updated every minute.<br/>*Call then returned to queue</div>

<script type="text/template" id="filter_template">
    <input type="checkbox" id="filter_all" data-filter="all" <%= all %> > <label for="filter_all"><strong>All Modes</strong></label><br>
    <input type="checkbox" id="filter_notclosed" data-filter="notclosed" <%= notclosed %> > <label for="filter_notclosed"><strong>Not Closed</strong></label><br>
    <input type="checkbox" id="filter_activemodes" data-filter="activemodes" <%= activemodes %> > <label for="filter_activemodes"><strong>Active Modes</strong></label><br>
        <input type="checkbox" id="filter_after_call_work" class="active_modes" data-filter="after_call_work" <%= after_call_work %> > <label for="filter_after_call_work">After Call Work</label><br>
        <input type="checkbox" id="filter_busy_work" class="active_modes" data-filter="busy_work" <%= busy_work %> > <label for="filter_busy_work">Busy (Work)</label><br>
        <input type="checkbox" id="filter_calling" class="active_modes" data-filter="calling" <%= calling %> > <label for="filter_calling">Calling</label><br>
        <input type="checkbox" id="filter_on_call" class="active_modes" data-filter="on_call" <%= on_call %> > <label for="filter_on_call">On Call</label><br>
        <input type="checkbox" id="filter_on_callout" class="active_modes" data-filter="on_callout" <%= on_callout %> > <label for="filter_on_callout">On Callout</label><br>
        <input type="checkbox" id="filter_open" class="active_modes" data-filter="open" <%= open %> > <label for="filter_open">Open</label><br>
        <input type="checkbox" id="filter_outbound" class="active_modes" data-filter="outbound" <%= outbound %> > <label for="filter_outbound">Outbound</label><br>
    <input type="checkbox" id="filter_inactivemodes" data-filter="inactivemodes" <%= inactivemodes %> > <label for="filter_inactivemodes"><strong>Inactive Modes</strong></label><br>
        <input type="checkbox" id="filter_break" class="inactive_modes" data-filter="break" <%= onbreak %> > <label for="filter_break">Break</label><br>
        <input type="checkbox" id="filter_busy_away" class="inactive_modes" data-filter="busy_away" <%= busy_away %> > <label for="filter_busy_away">Busy (Away)</label><br>
        <input type="checkbox" id="filter_closed" class="inactive_modes" data-filter="closed" <%= closed %> > <label for="filter_closed">Closed</label><br>
        <input type="checkbox" id="filter_lunch" class="inactive_modes" data-filter="lunch" <%= lunch %> > <label for="filter_lunch">Lunch</label><br>
    <button id="close_filter">close</button>
</script>

<script type="text/template" id="agent_template">
  <tr class="agent_info">
    <td class="agent_order"><%= order %></td>
    <td class="agent_name"><%= name %></td>
    <td class="agent_mode"><%= mode %></td>
    <td class="agent_time"></td>
    <td class="agent_calls_accepted"><%= calls_accepted %></td>
    <td class="agent_agent_denied"><%= agent_denied %></td>
    <td class="agent_denied_other"><%= denied_other %></td>
    <td class="agent_no_answer_or_busy"><%= no_answer_or_busy %></td>
    <td class="agent_average_talk_time"><%= average_talk_time %></td>
  </tr>

      <tr class="agent_control">
      <td colspan="2">
            <div>
                <span class="bold">Last call received to<br />
                Call Distributor:</span>
            </div>
            <div class="last_call_unavailable">No data available</div>
            <div class="last_call_refreshing">Refreshing last call data...</div>
            <div class="agent_last_call_start_time"><%= last_call.start_time %></div>
            <div>Call from: <span class="agent_last_call_caller_id"><%= last_call.caller_id %></span></div>
            <div>Dialed number: <span class="agent_last_call_dnis"><%= last_call.dnis %></span></div>
      </td>
      <td>
          <span class="bold">Change Mode:</span>
          <select class="change_mode">
              <option>--select--</option>
              <option>OPEN</option>
              <option>CLOSED</option>
              <option>LUNCH</option>
              <option>BREAK</option>
              <option>BUSY (AWAY)</option>
              <option>BUSY (WORK)</option>
              <option>OUTBOUND</option>
           </select>
      </td>
      <td colspan="3">
              </td>
      <td colspan="2">
          <span class="bold">Send Message:</span><br />
          <input type="text" class="send_message"/>
          <button class="send_button">Send</button>
      </td>
      <td class="hide_control">
          <a href="#">hide</a>
      </td>
    </tr>
  </script>

<script type="text/template" id="queued_call_template">
      <td class="queue_ani">
        <%= friendly_phone_format(ani) %>
        <a href="#" class="queue_remove"><img src="images/delete.png" alt="Remove from Queue"/></a>      </td>
      <td class="queue_wait_time"></td>
</script>

</body>
</html>
