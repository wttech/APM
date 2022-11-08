ace.define("ace/mode/cqsm_highlight_rules",["require","exports","module","ace/lib/oop","ace/mode/text_highlight_rules"], function(require, exports, module) {
	"use strict";

	var oop = require("../lib/oop");
	var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;

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

	var CqsmHighlightRules = function () {
		var keywords = ["begin", "BEGIN", "end", "END", "in", "IN", "as", "AS", "on", "ON"];

		for (var i = 0; i < CqsmReference.length; i++) {
			var reference = CqsmReference[i];
			keywords.push(reference.name.toUpperCase());
			keywords.push(reference.name.toLowerCase());
		}

		var keywordMapper = this.createKeywordMapper({
			"keyword": keywords.join("|"),
		}, "identifier", false);

		var start = [{
			token : "comment",
			regex : "#.*$"
		}, {
			token : "string",
			regex : "'.*?'"
		}, {
			token : "string",
			regex : "\".*?\""
		}, {
			token : "string",
			regex : "\\$\\{.*?\\}"
		}, {
			token : "paren.lparen",
			regex : "[\\[]"
		}, {
			token : "paren.rparen",
			regex : "[\\]]"
		}, {
			token : keywordMapper,
			regex : "[A-Za-z_$][A-Za-z0-9_\\-$]*\\b"
		}];

		var rules = {
			"start" : start
		};

		this.$rules = rules;
		this.normalizeRules();
	};
	
	oop.inherits(CqsmHighlightRules, TextHighlightRules);
	exports.CqsmHighlightRules = CqsmHighlightRules;
});

ace.define("ace/mode/cqsm", ["require", "exports", "module", "ace/lib/oop", "ace/mode/text", "ace/mode/cqsm_highlight_rules", "ace/range"], function (require, exports, module) {
	"use strict";
	
	var oop = require("../lib/oop");
	var TextMode = require("./text").Mode;
	var CqsmHighlightRules = require("./cqsm_highlight_rules").CqsmHighlightRules;
	var Range = require("../range").Range;
	
	var Mode = function() {
		this.HighlightRules = CqsmHighlightRules;
	};
	oop.inherits(Mode, TextMode);
	
	(function() {
		this.lineCommentStart = "#";
		this.$id = "ace/mode/cqsm";
	}).call(Mode.prototype);
	
	exports.Mode = Mode;
});