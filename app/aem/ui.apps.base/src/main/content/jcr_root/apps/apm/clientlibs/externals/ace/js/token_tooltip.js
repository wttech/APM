ace.define("ace/token_tooltip", ["require", "exports", "module"], function (require, exports, module) {
	"use strict";

	var dom = require("ace/lib/dom");
	var oop = require("ace/lib/oop");
	var event = require("ace/lib/event");
	var Range = require("ace/range").Range;
	var Tooltip = require("ace/tooltip").Tooltip;

	function loadJson(path, success, error)
	{
		var xhr = new XMLHttpRequest();

		xhr.open("GET", path, false);
		xhr.send();

		if (xhr.status === 200) {
			return JSON.parse(xhr.responseText);
		} else {
			return null;
		}
	}

	var CqsmReference = loadJson("/bin/apm/references");

	function findTokenReference(token) {
		if (!token || !token.value) {
			return null;
		}

		if (token.type === 'identifier' || token.type === 'keyword') {
			for (var i = 0; i < CqsmReference.length; i++) {
				if (CqsmReference[i].name.toUpperCase() === token.value.toUpperCase()) {
					return CqsmReference[i];
				}
			}
		}

		// for (var i = 0; i < CqsmReference.length; i++) {
		// 	for (var j = 0; j < CqsmReference[i].pattern.length; j++) {
		// 		var regex = new RegExp("^\\s*" + CqsmReference[i].pattern[j] + "\\s*$");
		// 		if (regex.test(token.value)) {
		// 			CqsmReference[i].which = j;
		//
		// 			return CqsmReference[i];
		// 		}
		// 	}
		// }

		return null;
	}

	function wordWrap(str, width, brk, cut) {
		brk = brk || '\n';
		width = width || 75;
		cut = cut || false;

		if (!str) { return str; }
		var regex = '.{1,' +width+ '}(\\s|$)' + (cut ? '|.{' +width+ '}|.+$' : '|\\S+?(\\s|$)');

		return str.match( RegExp(regex, 'g') ).join( brk );
	}

	function TokenTooltip (editor) {
		if (editor.tokenTooltip)
			return;
		Tooltip.call(this, editor.container);
		editor.tokenTooltip = this;
		this.editor = editor;

		this.update = this.update.bind(this);
		this.onMouseMove = this.onMouseMove.bind(this);
		this.onMouseOut = this.onMouseOut.bind(this);
		event.addListener(editor.renderer.scroller, "mousemove", this.onMouseMove);
		event.addListener(editor.renderer.content, "mouseout", this.onMouseOut);
	}

	oop.inherits(TokenTooltip, Tooltip);

	(function(){
		this.token = {};
		this.range = new Range();

		this.update = function() {
			this.$timer = null;

			var r = this.editor.renderer;
			if (this.lastT - (r.timeStamp || 0) > 1000) {
				r.rect = null;
				r.timeStamp = this.lastT;
				this.maxHeight = window.innerHeight;
				this.maxWidth = window.innerWidth;
			}

			var canvasPos = r.rect || (r.rect = r.scroller.getBoundingClientRect());
			var offset = (this.x + r.scrollLeft - canvasPos.left - r.$padding) / r.characterWidth;
			var row = Math.floor((this.y + r.scrollTop - canvasPos.top) / r.lineHeight);
			var col = Math.round(offset);

			var screenPos = {row: row, column: col, side: offset - col > 0 ? 1 : -1};
			var session = this.editor.session;
			var docPos = session.screenToDocumentPosition(screenPos.row, screenPos.column);
			var token = session.getTokenAt(docPos.row, docPos.column);
			var reference = findTokenReference(token);

			if (!token && !session.getLine(docPos.row)) {
				token = {
					type: "",
					value: "",
					state: session.bgTokenizer.getState(0)
				};
			}

			if (!token || !reference) {
				session.removeMarker(this.marker);
				this.hide();
				return;
			}

			var tokenText = wordWrap(reference.description);
			tokenText += '\n\nExamples:\n' + reference.examples.join('\n');
			// tokenText += "\nArguments: " + reference.args.join(", ");

			if (this.tokenText != tokenText) {
				this.setText(tokenText);
				this.width = this.getWidth();
				this.height = this.getHeight();
				this.tokenText = tokenText;
			}

			this.show(null, this.x, this.y);

			this.token = token;
			session.removeMarker(this.marker);
			this.range = new Range(docPos.row, token.start, docPos.row, token.start + token.value.length);
			this.marker = session.addMarker(this.range, "ace_bracket", "text");
		};

		this.onMouseMove = function(e) {
			this.x = e.clientX;
			this.y = e.clientY;
			if (this.isOpen) {
				this.lastT = e.timeStamp;
				this.setPosition(this.x, this.y);
			}
			if (!this.$timer)
				this.$timer = setTimeout(this.update, 100);
		};

		this.onMouseOut = function(e) {
			if (e && e.currentTarget.contains(e.relatedTarget))
				return;
			this.hide();
			this.editor.session.removeMarker(this.marker);
			this.$timer = clearTimeout(this.$timer);
		};

		this.setPosition = function(x, y) {
			if (x + 10 + this.width > this.maxWidth)
				x = window.innerWidth - this.width - 10;
			if (y > window.innerHeight * 0.75 || y + 20 + this.height > this.maxHeight)
				y = y - this.height - 30;

			Tooltip.prototype.setPosition.call(this, x + 10, y + 20);
		};

		this.destroy = function() {
			this.onMouseOut();
			event.removeListener(this.editor.renderer.scroller, "mousemove", this.onMouseMove);
			event.removeListener(this.editor.renderer.content, "mouseout", this.onMouseOut);
			delete this.editor.tokenTooltip;
		};

	}).call(TokenTooltip.prototype);

	exports.TokenTooltip = TokenTooltip;
});