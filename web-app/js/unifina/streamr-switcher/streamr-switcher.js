(function(exports) {

	function StreamrSwitcher(parent, json) {
		this.parent = $(parent)
		if(json)
			this.checked = json.switcherValue
	}

	StreamrSwitcher.prototype.render = function() {
		var _this = this
		this.switcher = $('<input type="checkbox" data-class="switcher-lg" class="streamr-switcher-checkbox">')
		this.switcher.attr("checked", this.checked)
		this.switcher.change(function(){
			_this.sendValue(_this.getValue())
		})
		this.parent.append(this.switcher)
		this.parent.addClass("switcher-container")
		this.switcher.switcher({
			theme: 'square',
			on_state_content: "1",
			off_state_content: "0"
		})
	}

	StreamrSwitcher.prototype.getDragCancelAreas = function() {
		return ["div.switcher-inner"]
	}

	StreamrSwitcher.prototype.getValue = function() {
		return this.switcher.prop('checked')
	}

	StreamrSwitcher.prototype.toJSON = function() {
		return {
			switcherValue: this.getValue()
		}
	}

	StreamrSwitcher.prototype.sendValue = function(value) {
		$(this).trigger("input", value)
	}

	StreamrSwitcher.prototype.receiveResponse = function(p) {
		if (p["switcherValue"] === true && !this.getValue())
			this.switcher.switcher("on")
		else if (p["switcherValue"] === false && this.getValue())
			this.switcher.switcher("off")
	}

exports.StreamrSwitcher = StreamrSwitcher

})(typeof(exports) !== 'undefined' ? exports : window)