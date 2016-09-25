/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 - 2016 Cognifide Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
/* framework.js */
var Cog = (function($, document) {
	var api = {},
		modules = {},
		modulesQueue = [],
		events = {},
		defaults = {
			noOperation: $.noop,
			emptyArray: [],
			listener: {
				fn: $.noop(),
				scope: api,
				params: {
					data: {},
					eventData: {}
				}
			},
			delegates: {
				init: 'init',
				beforeInit: 'beforeInit',
				load: 'load'
			}
		},
		scheduled = {
			init: false,
			load: false
		},
		isLoaded = false;

	function eachItem(items, callback, finallyCallback) {
		var key,
			callbackResult;

		callback = callback || defaults.noOperation;
		finallyCallback = finallyCallback || defaults.noOperation;

		try {
			for (key in items) {
				if (items.hasOwnProperty(key)) {
					callbackResult = callback(items[key]);
					if (callbackResult === false) {
						break;
					}
				}
			}
		} finally {
			finallyCallback();
		}
	}

	function init(options) {
		eachItem(options.targetModules, function(module) {
			module.beforeInit();
		});

		eachItem(options.targetModules, function(module) {
			module.init(options.elementsMap[module.name]);
		}, function() {
			scheduled.init = false;
		});
	}

	function scheduleInit(deferredFetch) {
		scheduled.init = true;

		api.ready(function() {
			deferredFetch.done(init);
		});
	}

	function load(options) {
		eachItem(options.targetModules, function(module) {
			module.load(options.elementsMap[module.name]);
		}, function() {
			scheduled.load = false;
		});
	}

	function scheduleLoad(deferredFetch) {
		scheduled.load = true;

		if (!isLoaded) {
			api.load(function() {
				deferredFetch.done(load);
			});
		}
		else {
			deferredFetch.done(load);
		}
	}

	/*
	 *	Adds custom event for a module.
	 *	@param {string} module Name of the module that registers the event
	 *	@param {string} evtName Name of the event
	 */
	function addEvent(moduleName, evtName){
		if (moduleName && evtName) {
			if (!events[moduleName]) {
				events[moduleName] = {};
			}

			if (!events[moduleName][evtName]) {
				events[moduleName][evtName] = [];
			}
		}
	}

	function canRegisterModule(requiredModules) {
		var canRegister = true;

		eachItem(requiredModules, function(name) {
				canRegister = modules[name] != null;
				return canRegister;
			});

		return canRegister;
	}

	function registerQueuedModules() {
		var queue = [],
			anyRegistered = false;

		eachItem(modulesQueue, function(module) {
			if (canRegisterModule(module.requires)) {
				modules[module.name] = module;
				anyRegistered = true;
			}
			else {
				queue.push(module);
			}
		});
		modulesQueue = queue;

		if (anyRegistered && modulesQueue.length > 0) {
			registerQueuedModules();
		}
	}

	function selectTargetModules(options) {
		var targetModules = {};

		eachItem(options.modules, function(name) {
			if (modules[name]) {
				targetModules[name] = modules[name];
			}
		});

		return targetModules;
	}

	function createSelector(targetModules) {
		var selectors = [];

		eachItem(targetModules, function(module) {
			if (module.selector) {
				selectors.push(module.selector + ':not(.initialized)');
			}
		});

		return selectors.join(',');
	}

	function createElementsMap($element, targetModules) {
		var selector = createSelector(targetModules),
			$allElements = api.find($element, selector),
			elementsMap = {};

		eachItem(targetModules, function(module){
			elementsMap[module.name] = $allElements.filter(module.selector).addClass('initialized');
		});

		return elementsMap;
	}

	function selectModuleDelegate(options, delegateType) {
		return options[delegateType] || options.api[delegateType] || defaults.noOperation;
	}

	// namespace for components
	api.component = {};

	/**
	 * Register new module
	 * @param options - an object with required and optional properties of a module:
	 *  name - name of the module (required)
	 *  api - API object of the module (required)
	 *  init - init function for module, if not defined api.init will be used
	 *  beforeInit - beforeInit function for module, if not defined api.beforeInit will be used
	 *  requires - an array with names of required components.
	 *  selector - a jQuery selector
	 *  Note: If component A requires component B, component B cannot require component A.
	 */
	api.register = function (options) {
		var module,
			canRegister = true,
			moduleTarget;

		if (options.name && options.api && modules[options.name] == null) {
			module = {
				name: options.name,
				api: options.api,
				init: selectModuleDelegate(options, defaults.delegates.init),
				beforeInit: selectModuleDelegate(options, defaults.delegates.beforeInit),
				load: selectModuleDelegate(options, defaults.delegates.load),
				requires: options.requires || defaults.emptyArray,
				selector: options.selector || null
			};

			moduleTarget = canRegisterModule(module.requires) ? modules : modulesQueue;
			moduleTarget[module.name] = module;

			registerQueuedModules();
		}
	};

	/**
	 * Initializes all registered modules
	 * @param {object} option - Optional config object. It can contain '$element' property with jQuery object of a container
	 *   element and/or 'modules' property - array with modules to init
	 */
	api.init = function (options){
		var deferredFetch = null;

		if (!scheduled.load || !scheduled.init) {
			deferredFetch = $.Deferred();

			scheduleInit(deferredFetch);
			scheduleLoad(deferredFetch);

			api.ready(function() {
				var $element = (options && options.$element) ? options.$element : $('body'),
					elementsMap,
					targetModules = modules;

				if (options && options.modules) {
					targetModules = selectTargetModules(options);
				}

				elementsMap = createElementsMap($element, targetModules);

				deferredFetch.resolve({
					targetModules: targetModules,
					elementsMap: elementsMap
				});
			});
		}

	};

	/**
	 * Attaches event listener to any event
	 * @param {string} moduleName Name of the module that registered the event
	 * @param {string} evtName Name of the event to attach the listener to
	 * @param {function} fn reference to the listener
	 * @param {object} options Optional object with special options of attaching listener.
	 *   This might contain scope property which enables to control the 'scope' of the
	 *   listener and/or 'data' property - an object with parameters passed to the listener
	 */
	api.addListener = function(moduleName, evtName, fn, options) {
		var listener;

		if (moduleName && evtName && typeof(fn) === 'function') {
			if (!(events[moduleName] && events[moduleName][evtName])) {
				addEvent(moduleName, evtName);
			}

			listener = $.extend(true, {}, defaults.listener, {
				fn: fn
			});
			if (options && options.scope) {
				listener.scope = options.scope;
			}
			if (options && options.data) {
				listener.params.data = options.data;
			}

			events[moduleName][evtName].push(listener);
		}
	};

	/*
	 *	Fires any defined event
	 *	@param {string} moduleName Name of the module that registered the event
	 *	@param {string} evtName Name of the event to attach the listener to
	 *	@param {object} eventData Optional object containig special parameters passed to listeners
	 */
	api.fireEvent = function(module, evtName, eventData) {
		var i,
			listener,
			params;

		if (events[module] && events[module][evtName]) {
			for (i = 0; i < events[module][evtName].length; i++) {
				listener = events[module][evtName][i];
				params = $.extend({}, listener.params);
				if (eventData) {
					params.eventData = eventData;
				}
				listener.fn.call(listener.scope, params);
			}
		}
	};

	/*
	 * Wrapper around $(document).ready - fires given function when (or if) document is ready.
	 */
	api.ready = function(fn) {
		$(document).ready(fn);
	};

	/*
	 * Wrapper around $(window).load - binds an event handler to the "load" JavaScript event.
	 * @param {object} $element jQuery object representing for example an image element.
	 * All supported HTML tags and JavaScript objects by load event can be found here: http://www.w3schools.com/jsref/event_onload.asp.
	 */
	api.load = function(fn) {
		$(window).load(fn);
	};

	/*
	 * Checks if @scope is a parent of $container.
	 * @param {object} $scope jQuery object representing a scope.
	 * @param {object} $container jQuery object.
	 * @return true if $scope is a parent of $container , false otherwise
	 */
	api.isInScope = function($scope, $container) {
		return $scope != null && $container != null && $container.parents().is($scope);
	};

	/*
	 * Finds all children objects matching selector and adds $scope if it matches selector
	 * @param {object} $scope jQuery object representing a scope.
	 * @param {object} selector jQuery selector.
	 * @return a set of matching elements
	 */
	api.find = function($scope, selector) {
		return $scope.find(selector).add($scope.filter(selector));
	};

	// Cookies
	// inspired by http://www.quirksmode.org/js/cookies.html
	api.Cookie = {
		create: function (name, value, days) {
			var expires = "",
				date;

			if (days) {
				date = new Date();
				date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
				expires = "; expires=" + date.toGMTString();
			}

			document.cookie = name + "=" + value + expires + "; path=/";
		},
		read: function (name) {
			var nameEQ = name + "=",
				ca = document.cookie.split(';'),
				c = null,
				i;

			for (i = 0; i < ca.length; i++) {
				c = ca[i];
				while (c.charAt(0)==' ') {
					c = c.substring(1, c.length);
				}
				if (c.indexOf(nameEQ) == 0) {
					return c.substring(nameEQ.length, c.length);
				}
			}
			return null;
		},
		erase: function (name) {
			api.cookie.create(name, "", -1);
		}
	};

	//initialization
	api.init();
	api.load(function() {
		isLoaded = true;
	});

	return api;
}(COGjQuery, document));
