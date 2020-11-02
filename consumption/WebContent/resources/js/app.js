q$ = jQuery;
q$(document).ready(function () {
	$('body').on('mousedown', '.ui-autocomplete-panel', function(event) { event.stopImmediatePropagation();});
	q$('.itemlinknav').click(function(e){
//		let protocol = location.protocol;
//		let slashes = protocol.concat("//");
//		let port=window.location.port;
//		let host = slashes.concat(window.location.hostname).concat(':').concat(port).concat('/norm/api/');
//		utility.doPostConnection(host+"palletlix?","cm=ping&dt={}","post", function() {
//		    console.log(e.target.href);
			window.location.href=e.target.href;
//			processData.showDialogOffline();
			
//		}, function() {
//			let r = confirm("Không có kết nối đến server bạn có muốn chuyển sang chế độ offline không, bấm Ok để chuyển qua chế độ offline.");
//			   if (r == true) {
//				   
//					processData.showDialogOffline();
//					return false;
//			   } 
//		})
//		e.preventDefault();
	});
	q$('#backhomeId').click(function(e){
		let protocol = location.protocol;
		let slashes = protocol.concat("//");
		let port=window.location.port;
		let host = slashes.concat(window.location.hostname).concat(':').concat(port).concat('/norm/api/');
		utility.doPostConnection(host+"palletlix?","cm=ping&dt={}","post", function() {
			console.log(e.target.href);
			window.location.href=e.target.href;
		}, function() {
			var r = confirm("Không có kết nối đến server bạn có muốn chuyển sang chế độ offline không!");
			   if (r == true) {
				   
					processData.showDialogOffline();
					return false;
			   } 
		})
		e.preventDefault();
	});
	q$('#mmdata01').click(function(e){
		document.getElementById("IdDataOfflineButton").click();
	});
});
// Offline.options = {
// // to check the connection status immediatly on page load.
// checkOnLoad: false,
//
// // to monitor AJAX requests to check connection.
// interceptRequests: true,
//
// // to automatically retest periodically when the connection is down (set to
// false to disable).
// reconnect: {
// // delay time in seconds to wait before rechecking.
// initialDelay: 3,
//
// // wait time in seconds between retries.
// delay: 10
// },
//
// // to store and attempt to remake requests which failed while the connection
// was down.
// requests: true
// };
// setTimeout(function a() {
// Offline.check()
// Offline.on('down', function() {
//  	
// });
// Offline.on('up', function() {
// });
// setTimeout(a, 10000);
// }, 10000);
var utility =function(){
	return {
		doPostAction:function(url,data1,method,callbackFunc){
			q$.ajax({
				url : url,
				type : method, // chọn phương thức gửi là post
				dataType : "text", // dữ liệu trả về dạng text
				data : data1,
				contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
				success : callbackFunc
					// Sau khi gửi và kết quả trả về thành công thì gán nội dung
					// trả về
					// đó vào thẻ div có id = re
			});
		},doPostConnection:function(url,data1,method,callBackSuccessFunc,callBackErrorFunc){
			$.ajax({
				   url : url,
				   type : method, // chọn phương thức gửi là post
				   dataType : "text", // dữ liệu trả về dạng text
				   data : data1,
				   success:callBackSuccessFunc,
				   error:callBackErrorFunc
// success :function(response){
// return false;
// },
// error: function(request,status,errorThrown) {
// var r = confirm("Không có kết nối đến server bạn có muốn chuyển sang chế độ
// offline không!");
// if (r == true) {
// document.getElementById("headerhide").style.display = "none";
// document.getElementById("menuhederhide").style.display = "none";
// document.getElementById("contentOnline").style.display = "none";
// document.getElementById("contentOffline").style.display = "block";
// return false;
// }
// }
				 });
		},
		setCookie:function(cname,cvalue,exdays){
			var d = new Date();
			 d.setTime(d.getTime() + (exdays*24*60*60*1000));
			 var expires = "expires=" + d.toGMTString();
			 document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/delivery";
			 return;
		},
		getCookie:function(cname){
			var name = cname + "=";
			var decodedCookie = decodeURIComponent(document.cookie);
			var ca = decodedCookie.split(';');
			for(var i = 0; i < ca.length; i++) {
			    var c = ca[i];
		       while (c.charAt(0) == ' ') {
			      c = c.substring(1);
			    }
			    if (c.indexOf(name) == 0) {
			      return c.substring(name.length, c.length);
			    }
			}
			  return "";
		},
		checkCookie:function(cname){
			 var user=utility.getCookie(cname);
			  if (user != "") {
			    return 0;
			  } else {
				  utility.setCookie("language", "vn", 30);
			  }
		},innerHTMLElement: function(ctext,cid){
			q$(cid).html(ctext);
		},clickFunction:function(id){
			document.getElementById(id).click();
		},customFilter:function(itemLabel,itemValue, filterValue){
			console.log(itemValue)
			itemLabel_create = itemLabel.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a").replace(/\ /g, '-').replace(/đ/g, "d").replace(/đ/g, "d").replace(/ỳ|ý|ỵ|ỷ|ỹ/g,"y").replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g,"u").replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ.+/g,"o").replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ.+/g, "e").replace(/ì|í|ị|ỉ|ĩ/g,"i");
			filterValue_create = filterValue.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a").replace(/\ /g, '-').replace(/đ/g, "d").replace(/đ/g, "d").replace(/ỳ|ý|ỵ|ỷ|ỹ/g,"y").replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g,"u").replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ.+/g,"o").replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ.+/g, "e").replace(/ì|í|ị|ỉ|ĩ/g,"i");
			 var result = itemLabel_create.search(filterValue_create);
			 if(result==-1){
				return false;
			 }else{
				return true;
			}
		},printPDF:function(base64){
	        var contentType = 'application/pdf';
		    var b64toBlob = (b64Data, contentType='', sliceSize=512) => {
		    	var byteCharacters = atob(b64Data);
		    	var byteArrays = [];

		    	  for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
		    		  var slice = byteCharacters.slice(offset, offset + sliceSize);

		    		var byteNumbers = new Array(slice.length);
		    	    for (let i = 0; i < slice.length; i++) {
		    	      byteNumbers[i] = slice.charCodeAt(i);
		    	    }

		    	    var byteArray = new Uint8Array(byteNumbers);
		    	    byteArrays.push(byteArray);
		    	  }
		    	    
		    	  var blob = new Blob(byteArrays, {type: contentType});
		    	  return blob;
		    }
		    var blob = b64toBlob(base64, contentType);
		    var blobUrl = URL.createObjectURL(blob);
		    printJS(blobUrl)
		},expandTable:function(clazz){
			let rowGroups =q$(clazz);
            for (let i = 0; i < rowGroups.length; i ++) {
                q$(rowGroups[i]).trigger('click');
            }
		}
	};
}();
var processData = function (){
	return {
		saveIdentifyMac:function(a){
			console.log(a)
			localStorage.setItem('identify', JSON.stringify(a));
		},
		showDialogOffline:function(){
			 q$('#myModalOffline').modal({
					backdrop: 'static'
			 });

			  q$('#myModalOffline .modal-dialog').draggable({
			    handle: ".modal-header,.modal-content"
			  });
		},offline:function(){
			document.getElementById("contentOnline").style.display = "none";
			document.getElementById("contentOffline").style.display = "block";
		},closeDialogOffline:function(){
				q$("#myModalOffline").modal("hide");
		},saveListProductLocal:function(){
			try{
				let protocol = location.protocol;
				let slashes = protocol.concat("//");
				let port=window.location.port;
				let host = slashes.concat(window.location.hostname).concat(':').concat(port).concat('/delivery/api/');
				utility.doPostAction(host+"productlix?","cm=list&dt={}","post",createObjList);
				function createObjList(result) {
					let obj=JSON.parse(result);
					if(obj.err==0){
						let listProduct=obj.dt.listproduct;
						// Put the object into storage
						localStorage.setItem('listProduct', JSON.stringify(listProduct));
					}
				}
			}catch(err){console.log(err);}
		},updateProductLocal:function(res){
			let obj=res.dt;
			let strListProduct=localStorage.getItem("listProduct");
			if(strListProduct !== undefined && strListProduct !=null && strListProduct !="" && strListProduct != "undefined"){
				let listProduct=JSON.parse(strListProduct);
				for(let p in listProduct){
					if(listProduct[p].id==obj.id){
						listProduct[p]=obj;
						localStorage.setItem("listProduct",JSON.stringify(listProduct));
						break;
					}
				}
			}
		},savePalletToLocal:function(res){
			if(res!==undefined && res!=null && res!=""){
				let obj=res.dt;
				let cStr=localStorage.getItem("listPalletLix");
				if(cStr === undefined || cStr == null || cStr=="" && cStr == "undefined"){
					let listTemp=[];
					listTemp.push(obj);
					localStorage.setItem("listPalletLix",JSON.stringify(listTemp));
				}else{
					let listPllet=[];
					listPllet=JSON.parse(cStr);
					listPllet.push(obj);
					localStorage.setItem("listPalletLix",JSON.stringify(listPllet));
				}
			}
		},getListPalletM:function(){
			let str_iden=localStorage.getItem("identify");
			if(str_iden !=undefined && str_iden != null && str_iden != "" && str_iden !="undefined"){
				let objtemp=encodeURIComponent(JSON.stringify({identify:str_iden}));
				let protocol = location.protocol;
				let slashes = protocol.concat("//");
				let port=window.location.port;
				let host = slashes.concat(window.location.hostname).concat(':').concat(port).concat('/delivery/api/');
				utility.doPostAction(host+"palletlix?","cm=list_pallet_in_shift&dt="+objtemp,"post",palletOfflineToLocal);
				function palletOfflineToLocal(response){
					let objres5=JSON.parse(response);
					if(objres5!==undefined && objres5!=null){
						if(objres5.err==0){
							let listyy=objres5.dt.listpalletlix;
							console.log(listyy);
							localStorage.setItem("listPalletLix",JSON.stringify(listyy));
						}
					}
				}
			}
		},editPalletToLocal:function(res){
			if(res!==undefined && res!=null && res !=""){
				let obj1=res.dt;
				let cStr1 = localStorage.getItem("listPalletLix");
				if(cStr1 !== undefined && cStr1 !=null && cStr1 != "undefined"){
					let listTemp1 = JSON.parse(cStr1);
					for(let index in listTemp1){
						if(listTemp1[index].id==obj1.id){
							listTemp1[index]=obj1;
							localStorage.setItem("listPalletLix",JSON.stringify(listTemp1));
							break;
						}
					}
				}
				
			}
		},deletePalletLocal:function(res){
			if(res!==undefined && res !=null && res !=""){
				let delObj=res.dt;
				let strList=localStorage.getItem("listPalletLix");
				if(strList !== undefined && strList !=null && strList!="" && strList != "undefined"){
					let listTemp2 = JSON.parse(strList);
					for(let i=0;i<listTemp2.length;i++){
						if(delObj.id == listTemp2[i].id){
							listTemp2.splice(i, 1);
							localStorage.setItem("listPalletLix",JSON.stringify(listTemp2));
							break;
						}
					}
				}
			}
		},deleteLocalDeliveryOff:function(res){
			if(res !== undefined && res !=null && res !=""){
				if(res.err==0){
					let obj=res.dt.listID;
					let strp=localStorage.getItem("listPalletLix");
					if(strp !== undefined && strp !=null && strp !="" && strp != "undefined"){
						let list = JSON.parse(strp);
						let leng=list.length;
						while(leng--){
							if(obj.indexOf(list[leng].id)!=-1){
								list.splice(leng,1);
							}
						}
						localStorage.setItem("listPalletLix",JSON.stringify(list));
					}
				}
			}
		},asynDataLocal:function(){
			let protocol = location.protocol;
			let slashes = protocol.concat("//");
			let port=window.location.port;
			let host = slashes.concat(window.location.hostname).concat(':').concat(port).concat('/delivery/api/');
			let strData=localStorage.getItem("listPalletLix");
			let strStringD=localStorage.getItem("listDelivery");
			if(strStringD !== undefined && strStringD !=null && strStringD !="" && strStringD != "undefined" ){
				let listm=JSON.parse(strStringD);
				let lobj={
						listInfo:listm
				}
				if(listm !==null && listm.length >0){
					let strsumary=encodeURIComponent(JSON.stringify(lobj));
					utility.doPostAction(host+"palletlix?","cm=save_delivery&dt="+strsumary,"post",syncDataDeliveryPalletOfflineApp);
					function syncDataDeliveryPalletOfflineApp(response){
						var objres1=JSON.parse(response);
						if(objres1.err==0){
							console.log("complete delivery....");
							let data=objres1.dt;
							let len=listm.length;
							while(len--){
								if(data.indexOf(listm[len].delivery.id)!=-1){
									listm.splice(len,1);
								}
							}
						    localStorage.setItem("listDelivery",JSON.stringify(listm));
							if(strData !== undefined && strData !=null && strData !="" && strData != "undefined"){
								let listPlletss=JSON.parse(strData);
								if(listPlletss !==null && listPlletss.length >0){
									utility.doPostAction(host+"palletlix?","cm=save_pallet&dt="+encodeURIComponent(JSON.stringify({listPalletLix:listPlletss})),"post",syncDataListPalletOfflineAppp);
									function syncDataListPalletOfflineAppp(response){
										let objres2=JSON.parse(response);
										if(objres2.err==0){
											let arrayDelete=objres2.dt;
											if(arrayDelete !== undefined && arrayDelete !=null){
												for(let ik in arrayDelete){
													processData.removeListInLocalApp(arrayDelete[ik]);
												}
											}
											processData.getListPalletM();
											swaldesigntimer('Thành công!', 'đồng bộ thành công!','success',2000);
											console.log("complete pallet.....");
										}
									}
								}else{
									processData.getListPalletM();
								}
							}else{
								processData.getListPalletM();
							}
						}
					}
			    }else{
			    	if(strData !== undefined && strData !=null && strData !="" && strData != "undefined"){
						let listPlletsss1=JSON.parse(strData);
						if(listPlletsss1 !== null && listPlletsss1.length >0){
							utility.doPostAction(host+"palletlix?","cm=save_pallet&dt="+encodeURIComponent(JSON.stringify({listPalletLix:listPlletsss1})),"post",syncDataListPalletOfflineApp2);
							function syncDataListPalletOfflineApp2(response){
								console.log(response);
								let objres4=JSON.parse(response);
								if(objres4.err==0){
									let arrayDelete1=objres4.dt;
									if(arrayDelete1 !== undefined && arrayDelete1 !=null){
										for(let ik1 in arrayDelete1){
											processData.removeListInLocalApp(arrayDelete1[ik1]);
										}
									}
									processData.getListPalletM();
									swaldesigntimer('Thành công!', 'đồng bộ thành công!','success',2000);
								}
							}
					   }else{
						   processData.getListPalletM();
					   }
					}else{
						processData.getListPalletM();
					}
			    }
			}else{
				if(strData !== undefined && strData !=null && strData !="" && strData != "undefined"){
					let listPlletsss=JSON.parse(strData);
					if(listPlletsss != null && listPlletsss.length >0){
						utility.doPostAction(host+"palletlix?","cm=save_pallet&dt="+encodeURIComponent(JSON.stringify({listPalletLix:listPlletsss})),"post",syncDataListPalletOfflineApp2);
						function syncDataListPalletOfflineApp2(response){
							console.log(response);
							let objres3=JSON.parse(response);
							if(objres3.err==0){
								let arrayDelete=objres3.dt;
								if(arrayDelete !== undefined && arrayDelete !=null){
									for(let ik in arrayDelete){
										processData.removeListInLocalApp(arrayDelete[ik]);
									}
								}
								processData.getListPalletM();
								swaldesigntimer('Thành công!', 'đồng bộ thành công!','success',2000);
							}
						}
				   }else{
					   processData.getListPalletM();
				   }
				}else{
					processData.getListPalletM();
				}
			}
		},removeListInLocalApp:function(id){
			let strData1=localStorage.getItem("listPalletLix");
			if(strData1 !== undefined && strData1 !=null && strData1 !="" && strData1 != "undefined"){
				let listTempAdd=JSON.parse(strData1);
				for(let i in listTempAdd){
					if(id==listTempAdd[i].id){
						listTempAdd.splice(i,1);
						localStorage.setItem("listPalletLix",JSON.stringify(listTempAdd));
						break;
					}
				}
			}
		},saveListLocalBarcode:function(containObj){
			if(containObj != undefined && containObj !=null){
				let listBarcode=containObj.dt.listBarcodeCookie;
				if(listBarcode != undefined && listBarcode !=null && listBarcode.length>0){
					localStorage.setItem("listBarcode",JSON.stringify(listBarcode));
				}
			}
		},saveLocalBarcode:function(bar){
			let barcode=localStorage.getItem("listBarcode");
			if(barcode === undefined || barcode == null || barcode == "" || barcode == "undefined"){
				let list=[];
				list.push(bar);
			    localStorage.setItem("listBarcode",JSON.stringify(list));
			}else{
				let list=JSON.parse(barcode);
				if(list !=null && list.length){
					for(let v in list){
						if(list[v].barcode==bar.barcode){
							list[v]=bar;
							localStorage.setItem("listBarcode",JSON.stringify(list));
							return;
						}
					}
				}
				list.push(bar);
				localStorage.setItem("listBarcode",JSON.stringify(list));
			}
		},clearAllBarcode:function(){
			localStorage.setItem("listBarcode",JSON.stringify([]));
		},editLocalBarcode:function(bar){
			let barcode=localStorage.getItem("listBarcode");
			if(barcode!==undefined && barcode != null && barcode != "" && barcode != "undefined"){
				let list = JSON.parse(barcode);
				for(let i in  list){
					if(list[i].barcode==bar.barcode){
						list[i]=bar;
						localStorage.setItem("listBarcode",JSON.stringify(list));
						console.log(bar);
						break;
					}
				}
				
			}else{
				let listbarcodetep=[];
				listbarcodetep.push(bar);
				localStorage.setItem("listBarcode",JSON.stringify(listbarcodetep));
			}
		},deleteBarcode:function(bar){
			var barcode=localStorage.getItem("listBarcode");
			if(barcode!==undefined && barcode != null && barcode != "" && barcode != "undefined"){
				var list = JSON.parse(barcode);
				for(i in  list){
					if(list[i].barcode==bar.barcode){
						list.splice(i,1);
						localStorage.setItem("listBarcode",JSON.stringify(list));
						console.log(bar);
						break;
					}
				}
				
			}
		},checkConnect:function(){
			let protocol = location.protocol;
			let slashes = protocol.concat("//");
			let port=window.location.port;
			let host = slashes.concat(window.location.hostname).concat(':').concat(port).concat('/delivery/api/');
			utility.doPostConnection(host+"palletlix?","cm=ping&dt={}","post", function() {
			}, function() {
				let r = confirm("Không có kết nối đến server bạn có muốn chuyển sang chế độ offline không!");
				   if (r == true) {
					   
						processData.showDialogOffline();
						return false;
				   } 
			})
		},standardizedPallet:function(){
			let protocol = location.protocol;
			let slashes = protocol.concat("//");
			let port=window.location.port;
			let host = slashes.concat(window.location.hostname).concat(':').concat(port).concat('/delivery/api/');
			let strData1=localStorage.getItem("listPalletLix");
			if(strData1 !== undefined && strData1 !=null && strData1 !="" && strData1 != "undefined"){
				let listPallet=JSON.parse(strData1);
				let arrayId=[];
				for(let i in listPallet){
					arrayId.push(listPallet[i].id);
				}
				if(arrayId.length>0){
					utility.doPostAction(host+"palletlix?","cm=standardized&dt="+encodeURIComponent(JSON.stringify({arrayId})),"post",standardizedLocal);
					function standardizedLocal(response){
						console.log(response);
						let objres3=JSON.parse(response);
						if(objres3.err==0){
							let listResult=objres3.dt;
							if(listResult !=undefined && listResult !=null && listResult !=""){
								let leng=listPallet.length;
								while(leng--){
									if(listResult.indexOf(listPallet[leng].id)!=-1){
										listPallet.splice(leng,1);
									}
								}
								localStorage.setItem("listPalletLix",JSON.stringify(listPallet));
							}
						}
					}
				}
			}
		}
		
	}
}();

