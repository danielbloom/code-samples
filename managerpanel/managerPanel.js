/* Note this is an edited extract from a larger single page app */

    // Filter model: handles which agent modes are currently visible, 
    // including mode categories such as 'active modes', 'all' etc
    App.Filter = Backbone.Model.extend({
        activeModes: ["open", "busy_work", "after_call_work", "outbound", "on_call", "calling", "on_callout"],
        inactiveModes: ["closed", "lunch", "break", "busy_away"],
        defaults: {
            open: true,
            busy_work: true,
            after_call_work: true,
            outbound: true,
            calling: true,
            on_callout: true,
            on_call: true,
            closed: true,
            lunch: true,
            "break": true,
            busy_away: true
        },
        initialize: function () {
            this.view = new App.FilterView({
                model: this
            });
            this.bind("change", this.updateFilter, this)
        },
        updateFilter: function () {
            this.trigger("filter", this.toJSON())
        },
        filterChange: function (filter) {
            switch (filter) {
            case "all":
                this.setAll();
                break;
            case "activemodes":
                this.setActiveModes();
                break;
            case "inactivemodes":
                this.setInactive();
                break;
            case "notclosed":
                this.setNotclosed();
                break;
            default:
                var set_mode = {};
                set_mode[filter] = !this.get(filter);
                this.set(set_mode)
            }
        },
        isAll: function () {
            var filters =
                this.toJSON(),
                showAll = true;

            _.each(filters, function (value, key) {
                if (!value) showAll = false
            });
            return showAll
        },
        setAll: function () {
            var filters = this.toJSON(),
                showAll = !this.isAll(),
                filter_obj = this,
                set_mode = {};

            _.each(filters, function (value, key) {
                set_mode[key] = showAll;
                filter_obj.set(set_mode, {
                    silent: true
                })
            });
            this.trigger("change");
        },
        isActive: function () {
            var showActive = true,
                filter_obj = this;

            _.each(this.activeModes, function (key) {
                if (!filter_obj.get(key)) {
                    showActive = false;
                }
            });
            return showActive;
        },
        setActiveModes: function () {
            var showActive = !this.isActive(),
                set_mode = {};

            _.each(this.activeModes, function (key) {
                set_mode[key] = showActive;
                App.filter.set(set_mode, {
                    silent: true
                })
            });
            this.trigger("change")
        },
        isInactive: function () {
            var showInactive = true,
                filter_obj = this;

            _.each(this.inactiveModes, function (key) {
                if (!filter_obj.get(key)) showInactive = false
            });
            return showInactive
        },
        setInactive: function () {
            var showInactive = !this.isInactive(),
                set_mode = {};

            _.each(this.inactiveModes, function (key) {
                set_mode[key] = showInactive;
                App.filter.set(set_mode, {
                    silent: true
                })
            });
            this.trigger("change")
        },
        isNotclosed: function () {
            var filters = this.toJSON(),
                showNotclosed = true;

            _.each(filters, function (value, key) {
                if (key != "closed" && !value || key == "closed" && value) showNotclosed = false
            });
            return showNotclosed
        },
        setNotclosed: function () {
            var filters = this.toJSON(),
                showNotclosed = !this.isNotclosed(),
                set_mode = {};

            _.each(filters, function (value, key) {
                set_mode[key] = showNotclosed;
                App.filter.set(set_mode, {
                    silent: true
                });
            });

            this.set({
                closed: false
            }, {
                silent: true
            });
            this.trigger("change")
        },
        isCustom: function () {
            var showActives = 0,
                showActive = true,
                showInactives = 0,
                showInactive = true,
                filter_obj = this;

            _.each(this.activeModes, function (key) {
                if (filter_obj.get(key)) showActives++;
                else showActive = false
            });

            _.each(this.inactiveModes, function (key) {
                if (filter_obj.get(key)) {
                    showInactives++;
                } else {
                    showInactive = false;
                }
            });

            if (showActive && showInactives === 0 || showInactive && showActives === 0 || showActive && showInactive) {
                return false;
            } else {
                return true;
            }
        }
    });

    // Filter view: renders the GUI to select visible modes
    App.FilterView = Backbone.View.extend({
        template: _.template($("#filter_template").html()),
        el: $("#agent_filter"),
        events: {
            "click input": "toggleVisibleModes",
            "click #close_filter": "closeFilterGui"
        },
        initialize: function () {
            this.model.bind("change", this.render, this);
            this.render()
        },
        toggleVisibleModes: function (ev) {
            var filter = $(ev.currentTarget).data("filter");
            this.model.filterChange(filter)
        },
        closeFilterGui: function () {
            $("#agent_filter").hide()
        },
        render: function () {
            var model_json = this.model.toJSON(),
                custom = this.model.isCustom(),
                $agent_filter_status = $("#agent_filter_status"),
                $modes_filtered = $("#modes_filtered"),
                data = [];

            // for each mode combination, check or uncheck the checkbox
            $.each(model_json, function (mode, status) {
                if (mode == "break") {
                    data["onbreak"] = status ? "checked" : "";
                } else {
                    data[mode] = status ? "checked" : ""
                }
            });

            // check for each mode category (eg. 'active modes')
            data["all"] = this.model.isAll() ? "checked" : "";
            data["notclosed"] = this.model.isNotclosed() ? "checked" : "";
            data["activemodes"] = this.model.isActive() ? "checked" : "";
            data["inactivemodes"] = this.model.isInactive() ? "checked" : "";
            this.el.html(this.template(data));
            
            if (data["all"] == "checked") {
                $modes_filtered.hide();
                $agent_filter_status.html("All Modes")
            } else {
                $modes_filtered.show();
                if (data["notclosed"] == "checked") {
                    $agent_filter_status.html("Not Closed");
                } else if (custom) {
                    $agent_filter_status.html("Custom");
                } else if (data["activemodes"] == "checked") {
                    $agent_filter_status.html("Active Modes");
                } else if (data["inactivemodes"] == "checked") {
                    $agent_filter_status.html("Inactive Modes");
                }
            }
        }
    });

    App.Agent = Backbone.Model.extend({
        defaults: {
            time_in_mode: 0
        },
        initialize: function () {
            this.onChangeMode();
            this.view = new App.AgentView({
                model: this
            });
            this.bind("change:mode", this.onChangeMode, this);
            this.set({
                modeLastUpdated: new Date - this.get("time_in_mode")
            });
            this.unset("time_in_mode");
            if (App.monitoringEnabled) this.bind("change:mode", this.onChangeModeSetMonitorState)
        },
        onChangeMode: function () {
            this.set({
                modeLastUpdated: new Date;
            });
            this.trigger("modechange")
        }
    });

    // Agent list: collection of agents
    App.AgentList = Backbone.Collection.extend({
        model: App.Agent,
        currentComparator: "alpha_asc",
        initialize: function () {
            this.view = new App.AgentListView({
                model: this
            })
        },
        sort_by_weight: function () {
            currentComparator = "weight"
        },
        comparator: function (agent) {
            switch (App.agent_list.currentComparator) {
                case "alpha_asc":
                    return agent.get("name").toLowerCase();
                case "alpha_desc":
                    /*  This version of Backbone (0.5.3) does not allow comparators to be used as a sortBy
                    which makes reverse alphabetical sorting tricky. This solution compares the strings by
                    char code. Empty chars are appended to better handle comparisons where one agent
                    name is a substring of another agent name. */
                    var str = agent.get("name").toLowerCase() + "  ";
                    str = str.split("");
                    str = _.map(str, function (letter) {
                        return 65535 - letter.charCodeAt(0)
                    });
                    return str;
                case "queue":
                    return agent.get("order") >= 1 ? agent.get("order") : 9999;
                case "weight":
                    return 1E3 - agent.get("weight");
            }
        },
        reset: function (models, options) {
            var _this = this;
            _.each(_this.models, this.resetModel, _this);
            return Backbone.Collection.prototype.reset.call(this, models, options)
        },
        resetModel: function (model) {
            model.view.unbind();
            model.view.undelegate();
            model.view.remove();
            model.unbind();
            model.undelegate();
            model.clear({
                "silent": true
            })
        },
        setSort: function (sort_by) {
            this.currentComparator = sort_by;
            this.sort()
        }
    });

    App.AgentView = Backbone.View.extend({
        tagName: "tbody",
        template: _.template($("#agent_template").html()),
        filter: null,
        initialize: function () {
            var monitorNumber = $.cookie("live_monitor_number"),
                lastCall = this.model.get("last_call");
            this.filter = App.filter;
            this.formattedNumber = "";
            this.phoneFormatter = new i18n.phonenumbers.AsYouTypeFormatter("US");
            this.create();
            this.updateModeClass();
            this.model.bind("change", this.update, this);
            App.Timer.bind("tick", this.updateTimer, this);
            if (App.monitoringEnabled && 0 == App.isReadOnly) {
                if (monitorNumber) this.$(".monitor_number").val(monitorNumber);
                else this.$(".monitor_button.ready").prop("disabled", true);
                this.formatMonitorNumber();
                this.setMonitorState()
            }
        },
        events: {
            "click .agent_name,.agent_mode": "showAgentControl",
            "click .hide_control": "hideAgentControl",
            "change .change_mode": "remoteUpdateMode",
            "click .send_button": "remoteSendMessage",
            "click .monitor_button.ready": "monitorCall",
            "click .monitor_button.mute": "monitorMute",
            "click .monitor_button.unmute": "monitorUnmute",
            "keyup .monitor_number": "formatMonitorNumber",
            "cut .monitor_number": "formatMonitorNumber",
            "paste .monitor_number": "formatMonitorNumber"
        },
        create: function () {
            var attrs = this.model.toJSON();
            if (typeof attrs.average_talk_time !== "undefined") attrs.average_talk_time = seconds_to_hms(attrs.average_talk_time);
            $(this.el).html(this.template(attrs))
        },
        update: function () {
            var changedAttributes =
                this.model.changedAttributes(),
                $lastCallUnavailable = this.$(".last_call_unavailable");
            _.each(changedAttributes, function (value, attrib) {
                this.$(".agent_" + attrib).html(value)
            }, this);
            if (typeof changedAttributes.last_call != "undefined")
                if (false == changedAttributes.last_call) $lastCallUnavailable.show();
                else {
                    $lastCallUnavailable.hide();
                    _.each(changedAttributes.last_call, function (value, attrib) {
                        this.$(".agent_last_call_" + attrib).html(value)
                    }, this)
                }
            if (changedAttributes.average_talk_time) this.$(".agent_average_talk_time").html(seconds_to_hms(changedAttributes.average_talk_time));
            if (changedAttributes.mode) this.updateModeClass();
            if (changedAttributes.live_monitor) this.setMonitorState();
            return this
        },
        updateModeClass: function () {
            var mode = this.model.get("mode");
            if (!mode) {
                return;
            }
            mode = this.getModeClass();
            this.$("td.agent_mode").removeClass()
                                   .addClass("agent_mode")
                                   .addClass(mode);
            this.updateFilter(this.filter.toJSON());
            return this;
        },
        getModeClass: function () {
            var mode = this.model.get("mode");
            if (!mode) {
                return;
            }
            mode = mode.toLowerCase().replace(/\s/g, "_").replace(/[^_a-zA-Z]/g, "");
            return mode;
        },
        updateFilter: function () {
            var filter = this.filter.toJSON();
            if (filter[this.getModeClass()]) {
                $(this.el).show().addClass("filter_visible");
            } else {
                $(this.el).hide().removeClass("filter_visible");
            }
            this.model.trigger("zebra");
        },
        zebraStripe: function (state) {
            if (state == "even") {
                $(this.el).css({
                    "background-color": "#DCDCDC"
                });
            } else {
                $(this.el).css({
                    "background-color": "#FDF5E6"
                });
            }
        }
        
    });

    App.AgentListView = Backbone.View.extend({
        el: $("#agent_list table"),
        events: {
            "click .sort_by_queue": "sortByQueue",
            "click .sort_by_alpha": "sortByAlpha"
        },
        initialize: function () {
            this.model.bind("add", this.addAgent, this);
            this.model.bind("reset", this.resetAgents, this);
            this.model.bind("zebra", this.zebraStripe, this);
            this.filter = App.filter;
            this.filter.bind("filter", this.renderFilterUpdate, this);
        },
        resetAgents: function () {
            this.$("tbody").unbind();
            this.$("tbody").undelegate();
            this.$("tbody").remove();
            _.each(this.model.models, this.addAgent, this);
        },
        addAgent: function (agent) {
            $(this.el).append(agent.view.render().el);
            agent.view.delegateEvents(agent.view.events);
        },
        renderFilterUpdate: function () {
            _.each(this.model.models, function (model) {
                var view = model.view,
                    filter = view.filter.toJSON();
                if (filter[view.getModeClass()]) {
                    $(view.el).show().addClass("filter_visible");
                } else {
                    $(view.el).hide().removeClass("filter_visible");
                }
            });
            this.zebraStripe()
        },
        zebraStripe: function () {
            var rows = $("tbody.filter_visible");
            _.each(rows, function (row, index) {
                if (index % 2) {
                    $(row).css({
                        "background-color": "#DCDCDC"
                    });
                } else {
                    $(row).css({
                        "background-color": "#FDF5E6"
                    });
                }
            });
        },
        sortByQueue: function () {
            this.model.setSort("queue");
            this.zebraStripe();
        },
        sortByAlpha: function () {
            var sort_by = this.model.currentComparator != "alpha_asc" ? "alpha_asc" : "alpha_desc";
            this.model.setSort(sort_by);
            this.zebraStripe();
        }
    });

   
    App.filter = new App.Filter;
    App.agent_list = new App.AgentList;
    App.queued_calls = new App.QueuedCalls;
    App.queue_stats = new App.QueueStats;
    var cometClientId = null;

   //*******//
