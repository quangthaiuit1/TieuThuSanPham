app.service('MyService', function($http, $rootScope) {
	    var service={};
	    service.getListObjectWithParam=getListObjectWithParam;
	    service.addObject=addObject;
	    service.updateObject=updateObject;
	    service.addObjectWithParam=addObjectWithParam;
	    service.change_currency=change_currency;
	    service.checkServerOffline=checkServerOffline;
	    format_money="#.##0,0";
		 function getListObjectWithParam(table, command, dataJson,resultCallBack) {
			let cmd = command;
			let protocol = location.protocol;
			let slashes = protocol.concat("//");
			let port=window.location.port;
			let host = slashes.concat(window.location.hostname).concat(':').concat(port).concat('/delivery/api/');
			let req = {
				method : 'POST',
				url : host + table + "?",
				headers : {
					'Content-Type' : "application/x-www-form-urlencoded; charset=UTF-8"
				},
				data : $.param({
					cm : cmd,
					dt : dataJson
				})
			}

			  $http(req).then(function(response) {
				if (response.err === 403) {
					console.log("api bi loi roi !!!")
				}else{
					resultCallBack(response);
				}

			});
		}
		function checkServerOffline(table, command, dataJson,resultCallBack,errorCallBack){
			 let cmd = command;
			 let protocol = location.protocol;
				let slashes = protocol.concat("//");
				let port=window.location.port;
				let host = slashes.concat(window.location.hostname).concat(':').concat(port).concat('/delivery/api/');
				let req = {
					method : 'POST',
					url : host + table + "?",
					headers : {
						'Content-Type' : "application/x-www-form-urlencoded; charset=UTF-8"
					},
					data : $.param({
						cm : cmd,
						dt : dataJson
					})
				}

				  $http(req).then(function(response) {
					if (response.err === 403) {
						console.log("api bi loi roi !!!")
					}else{
						resultCallBack(response);
					}

				},function(err){
					errorCallBack(err);
				});
		 }
		 function addObject(table, dataJson,resultCallBack) {
		        let cmd = "add"
		        let protocol = location.protocol;
				let slashes = protocol.concat("//");
				let port=window.location.port;
				let host = slashes.concat(window.location.hostname).concat(':').concat(port).concat('/delivery/api/');
		        let req = {
		            method: 'POST',
		            url: host + table + "?",
		            headers: {
		                'Content-Type': "application/x-www-form-urlencoded; charset=UTF-8"
		            },
		            data: $.param({cm: cmd, dt: dataJson})
		        }
		        $http(req).then(function(response) {
					if (response.err === 403) {
						console.log("api bi loi roi !!!")
					}else{
						resultCallBack(response);
					}

				});
		    }
		 function updateObject(table, dataJson,resultCallBack) {
		        let cmd = "update"
		        let protocol = location.protocol;
				let slashes = protocol.concat("//");
				let port=window.location.port;
				let host = slashes.concat(window.location.hostname).concat(':').concat(port).concat('/delivery/api/');
		        let req = {
		            method: 'POST',
		            url: host + table + "?",
		            headers: {
		                'Content-Type': "application/x-www-form-urlencoded; charset=UTF-8"
		            },
		            data: $.param({cm: cmd, dt: dataJson})
		        }
		        $http(req).then(function(response) {
					if (response.err === 403) {
						console.log("api bi loi roi !!!")
					}else{
						resultCallBack(response);
					}

				});
		    }
		 function addObjectWithParam(table, cmd, dataJson,resultCallBack) {
		        let command = cmd;
		        let protocol = location.protocol;
				let slashes = protocol.concat("//");
				let port=window.location.port;
				let host = slashes.concat(window.location.hostname).concat(':').concat(port).concat('/delivery/api/');
		        let req = {
		            method: 'POST',
		            url: host + table + "?",
		            headers: {
		                'Content-Type': "application/x-www-form-urlencoded; charset=UTF-8"
		            },
		            data: $.param({cm: command, dt: dataJson})
		        }
		        $http(req).then(function(response) {
					if (response.err === 403) {
						console.log("api bi loi roi !!!")
					}else{
						resultCallBack(response);
					}

				});
		    }
	    function change_currency(value) {
	        var value1 = String(value).replace(/[,]/g, '');
	        if (isNaN(value1) === false)
	        {

	            if (format_money === "#,##0")
	            {
	                value1 = Math.round(parseFloat(value1));
	                value1 += '';
	                var x = value1.split('.');
	                var x3 = "";
	                if (x.length > 1)////trường hợp 1.0 .0
	                {
	                    x3 = x[1];
	                    if (parseInt(x3) === 0)
	                    {
	                        var xtemp = x[0];
	                        var x = xtemp.split('.');
	                        var x1 = x[0];
	                        var x2 = x.length > 1 ? '.' + x[1] : '';
	                        var rgx = /(\d+)(\d{3})/;
	                        while (rgx.test(x1))
	                            x1 = x1.replace(rgx, '$1' + ',' + '$2');
	                        value = x1 + x2;
	                        return value;
	                    }

	                }
	                var x1 = x[0];
	                var x2 = x.length > 1 ? '.' + x[1] : '';
	                var rgx = /(\d+)(\d{3})/;
	                while (rgx.test(x1))
	                    x1 = x1.replace(rgx, '$1' + ',' + '$2');
	                value = x1 + x2;
	                return value
	            } else if (format_money === "#.##0,0")
	            {
	                value1 = (parseFloat(value1)).toFixed(1);
	                value1 += '';
	                var x = value1.split('.');
	                var x3 = "";
	                if (x.length > 1)////trường hợp 1.0 .0
	                {
	                    x3 = x[1];
	                    if (parseInt(x3) === 0)
	                    {
	                        var xtemp = x[0];
	                        var x = xtemp.split('.');
	                        var x1 = x[0];
	                        var x2 = x.length > 1 ? '.' + x[1] : '';
	                        var rgx = /(\d+)(\d{3})/;
	                        while (rgx.test(x1))
	                            x1 = x1.replace(rgx, '$1' + ',' + '$2');
	                        value = x1 + x2;
	                        return value;
	                    }

	                }
	                var x1 = x[0];
	                var x2 = x.length > 1 ? '.' + x[1] : '';
	                var rgx = /(\d+)(\d{3})/;
	                while (rgx.test(x1))
	                    x1 = x1.replace(rgx, '$1' + ',' + '$2');
	                value = x1 + x2;
	                return value
	            } else if (format_money === "#,##0.00")
	            {
	                value1 = (parseFloat(value1)).toFixed(2);
	                value1 += '';
	                var x = value1.split('.');
	                var x3 = "";
	                if (x.length > 1)////trường hợp 1.0 .0
	                {
	                    x3 = x[1];
	                    if (parseInt(x3) === 0)
	                    {
	                        var xtemp = x[0];
	                        var x = xtemp.split('.');
	                        var x1 = x[0];
	                        var x2 = x.length > 1 ? '.' + x[1] : '';
	                        var rgx = /(\d+)(\d{3})/;
	                        while (rgx.test(x1))
	                            x1 = x1.replace(rgx, '$1' + ',' + '$2');
	                        value = x1 + x2;
	                        return value;
	                    }

	                }
	                var x1 = x[0];
	                var x2 = x.length > 1 ? '.' + x[1] : '';
	                var rgx = /(\d+)(\d{3})/;
	                while (rgx.test(x1))
	                    x1 = x1.replace(rgx, '$1' + ',' + '$2');
	                value = x1 + x2;
	                return value
	            } else if (format_money === "#,##0.000")
	            {
	                value1 = (parseFloat(value1)).toFixed(3);
	                value1 += '';
	                var x = value1.split('.');
	                var x3 = "";
	                if (x.length > 1)////trường hợp 1.0 .0
	                {
	                    x3 = x[1];
	                    if (parseInt(x3) === 0)
	                    {
	                        var xtemp = x[0];
	                        var x = xtemp.split('.');
	                        var x1 = x[0];
	                        var x2 = x.length > 1 ? '.' + x[1] : '';
	                        var rgx = /(\d+)(\d{3})/;
	                        while (rgx.test(x1))
	                            x1 = x1.replace(rgx, '$1' + ',' + '$2');
	                        value = x1 + x2;
	                        return value;
	                    }

	                }
	                var x1 = x[0];
	                var x2 = x.length > 1 ? '.' + x[1] : '';
	                var rgx = /(\d+)(\d{3})/;
	                while (rgx.test(x1))
	                    x1 = x1.replace(rgx, '$1' + ',' + '$2');
	                value = x1 + x2;
	                return value
	            } else if (format_money === "#,##0.0000")
	            {
	                value1 = (parseFloat(value1)).toFixed(4);
	                value1 += '';
	                var x = value1.split('.');
	                var x3 = "";
	                if (x.length > 1)////trường hợp 1.0 .0
	                {
	                    x3 = x[1];
	                    if (parseInt(x3) === 0)
	                    {
	                        var xtemp = x[0];
	                        var x = xtemp.split('.');
	                        var x1 = x[0];
	                        var x2 = x.length > 1 ? '.' + x[1] : '';
	                        var rgx = /(\d+)(\d{3})/;
	                        while (rgx.test(x1))
	                            x1 = x1.replace(rgx, '$1' + ',' + '$2');
	                        value = x1 + x2;
	                        return value;
	                    }

	                }
	                var x1 = x[0];
	                var x2 = x.length > 1 ? '.' + x[1] : '';
	                var rgx = /(\d+)(\d{3})/;
	                while (rgx.test(x1))
	                    x1 = x1.replace(rgx, '$1' + ',' + '$2');
	                value = x1 + x2;
	                return value
	            }





	        } else
	        {
	            return 0
	        }
	    }
		 return service;
});