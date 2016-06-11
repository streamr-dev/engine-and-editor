(function() {

    var KeyValuePair = ListEditor.ValueInList.extend({
        defaults: {
            key: '',
            value: ''
        },
        isEmpty: function() {
            var v = model.get('key')
            return v === undefined || v === ""
        }
    });

    var KeyValuePairList = ListEditor.ValueList.extend({
        model: KeyValuePair,

        constructor: function(object) {
            var list = Object.keys(object).map(function(key) {
                return {
                    key: key,
                    value: object[key]
                }
            })
            ListEditor.ValueList.apply(this, [list]);
        },

        toJSON: function() {
            var list = ListEditor.ValueList.prototype.toJSON.apply(this);
            var map = {}
            list.forEach(function(item) {
                map[item.key] = item.value
            })
            return map
        }
    });

    var KeyValuePairView = Backbone.View.extend({
        tagName: 'tr',
        template: '<td><input type="text" class="form-control input-sm key" name="key" value="{{key}}" placeholder="Key"></td><td><input type="text" class="form-control input-sm value" name="value" value="{{value}}" placeholder="Value"></td><td><button class="btn btn-default btn-xs delete"><i class="fa fa-trash-o"></i></button></td>',
        events: {
            'click .delete': 'remove',
            'change input.key': 'update',
            'change input.value': 'update',
        },
        initialize: function() {
            this.listenTo(this.model, 'remove', this.unrender)
        },
        render: function() {
            this.$el.html(Mustache.render(this.template, this.model.attributes))
            return this
        },
        update: function() {
            this.model.set("key", $(this.el).find("input.key").val())
            this.model.set("value", $(this.el).find("input.value").val())
        },
        unrender: function(){
            $(this.el).remove();
        },
        remove: function(){
            this.model.collection.remove(this.model);
        }
    });
    // Speeds up rendering
    Mustache.parse(KeyValuePairView.template)

    var KeyValuePairEditor = Backbone.View.extend({
        // Wrap table in a form to get native tabbing
        template: "<table class='table table-striped table-condensed'><thead><tr><th>Key</th><th>Value</th><th><button class='btn btn-default btn-xs add'><i class='fa fa-plus'></i></button></th></tr></thead><tbody></tbody></table>",
        events: {
            'click .add': 'add'
        },
        initialize: function() {
            if (!this.collection) {
                this.collection = new KeyValuePairList()
            }

            this.listenTo(this.collection, 'add', this.append)
            this.render();
        },
        render: function() {
            var _this = this
            this.$el.html(Mustache.render(this.template))
            this.collection.models.forEach(function(model) {
                _this.append(model);
            });

            return this
        },
        getAddButton: function() {
          return this.$el.find(".add")
        },
        add: function() {
            var item = new KeyValuePair();
            this.collection.add(item);
        },
        append: function(model) {
            var view = new KeyValuePairView({
                model: model
            });
            this.$el.find('table tbody').append(view.render().el);
        },
        clear: function() {
            this.collection.remove(this.collection.toArray())
        },
        disable: function() {
            this.$el.hide()
        },
        enable: function() {
            this.$el.show()
        }
    });
    // Speeds up rendering
    Mustache.parse(KeyValuePairEditor.template)

    KeyValuePairEditor.KeyValuePair = KeyValuePair
    KeyValuePairEditor.KeyValuePairList = KeyValuePairList
    KeyValuePairEditor.KeyValuePairView = KeyValuePairView

    if (typeof module !== 'undefined' && module.exports)
        module.exports = KeyValuePairEditor
    else window.KeyValuePairEditor = KeyValuePairEditor

})()