	var IframeHttpRequest = function()
	{
		var o = this;
		var intervalId = null;
		var aborted = false;
		var frame = null;
		var head = new Array();
		
		this.UNSENT = 0;
		this.OPENED = 1;
		this.HEADERS_RECEIVED = 2;
		this.LOADING = 3;
		this.DONE = 4;
		
		this.url = "";
		this.responseText = "";
		this.readyState = this.UNSENT;
		this.status = 200;
		this.methodRequest = "GET";
		
		var done = function() {
			if(frame != null)
			{
				frame.stop();
				frame.parentNode.removeChild(frame);
				delete frame;
			}
			clearInterval(intervalId);
		};
		
		this.open = function(method, url, async) {
			this.url = url;
			this.readyState = this.OPENED;
			this.methodRequest = method;
		};
		
		this.setRequestHeader = function(key, value) {
			head.push({key: key, value: value});
		};
		
		this.send = function(data) {
			aborted = false;
			
			frame = document.createElement("iframe");
			frame.style.display = 'none';
			document.body.appendChild(frame);
			
			var fContent = frame.contentDocument || frame.contentWindow.document;
			
			var txtHead = '';
			for(i in head)
			{
				var h = head[i];
				txtHead += '<meta http-equiv="'+h.key+'" content="'+h.value+'">';
			}
			
			var myContent = '<!DOCTYPE html><html><head>'+txtHead+'</head><body></body></html>';
			
			fContent.open('text/html', 'replace');
			fContent.write(myContent);
			fContent.close();
			
			var form = document.createElement("form");
			form.setAttribute("method", this.methodRequest);
			form.setAttribute("action", this.url);
			
			var param = Greencode.jQuery.paramObject(data);
			param.push({key: "isAjax", value: true});
			for(var i in param)
			{
				var p = param[i];
				var input = document.createElement("input");
				input.setAttribute('type', 'hidden');
				input.setAttribute('name', p.key);
				input.setAttribute('value', p.value);
				form.appendChild(input);
			}
			
			fContent.body.appendChild(form);
			
			form.submit();
			
			this.readyState = this.HEADERS_RECEIVED;
			
			intervalId = setInterval(function() {
				if(aborted === true) return;
				try {
					fContent = frame.contentDocument || frame.contentWindow.document;
					
					if(fContent.documentElement != null)
					{	
						var element = fContent.getElementsByTagName('body')[0];
						o.responseText = element.innerText == null ? element.textContent : element.innerText;
					}
					
					if(o.responseText != null)
					{
						if(fContent.readyState === "loading")
							o.readyState = o.HEADERS_RECEIVED;
						else if(fContent.readyState === "interactive")
							o.readyState = o.LOADING;	
						else if(fContent.readyState === "complete")
						{
							o.readyState = o.DONE;
							done();
						}
						
						if(o.onreadystatechange != null)
						{
							o.onreadystatechange.call(o);
						}
					}
				} catch (e) {
					o.abort();
				}
			}, 15);
		};

		this.abort = function() {		
			this.status = 200;
			this.readyState = this.UNSENT;
			aborted = true;
			done();
		};
	};