var app = angular.module('myApp', ['datatables','datatables.scroller','datatables.directive','aw-picklist','angucomplete-alt','datatables.fixedcolumns']);
app.controller('palletLixController', function($scope, $http, $window,
		$rootScope, $http, MyService,DTOptionsBuilder,DTColumnDefBuilder) {
	$http.defaults.headers.common['Authorization'] = 'Bearer testxxxxxx';
	$scope.dtOptions = DTOptionsBuilder.newOptions().withOption('destroy',true).withOption('lengthChange', false).withOption('bFilter', false).withDOM('lfrti').withScroller().withOption('deferRender', true).withOption('scrollY', 400);
	$scope.dtColumnDefs= [
        DTColumnDefBuilder.newColumnDef(0).notSortable(),
        DTColumnDefBuilder.newColumnDef(1).notSortable(),
        DTColumnDefBuilder.newColumnDef(2).notSortable(),
        DTColumnDefBuilder.newColumnDef(3).notSortable(),
        DTColumnDefBuilder.newColumnDef(4).notSortable(),
        DTColumnDefBuilder.newColumnDef(5).notSortable(),
        DTColumnDefBuilder.newColumnDef(6).notSortable(),
        DTColumnDefBuilder.newColumnDef(7).notSortable(),
        DTColumnDefBuilder.newColumnDef(8).notSortable()
        ];
	$scope.dtOptions1 = DTOptionsBuilder.newOptions().withOption('order', [0, 'desc']).withOption('destroy',true).withOption('lengthChange', false).withOption('bFilter', false).withDOM('lfrti').withScroller().withOption('deferRender', true).withOption('scrollY', 400);
	$scope.dtColumnDefs2= [
        DTColumnDefBuilder.newColumnDef(0).notSortable(),
        DTColumnDefBuilder.newColumnDef(1).notSortable(),
        DTColumnDefBuilder.newColumnDef(2).notSortable(),
        DTColumnDefBuilder.newColumnDef(3).notSortable(),
        DTColumnDefBuilder.newColumnDef(4).notSortable(),
        DTColumnDefBuilder.newColumnDef(5).notSortable(),
        DTColumnDefBuilder.newColumnDef(6).notSortable(),
        DTColumnDefBuilder.newColumnDef(7).notSortable()
        ];
	/* tab3 */
	$scope.listProduct = [];
	$scope.productBCSelected = [];
	$scope.listShiftLix=[];
	$scope.listWorkshopLix=[];
	$scope.listTeamLix=[];
	$scope.shiftLixSelect=0;
	$scope.workshopLixSelect = 0;
	$scope.teamLixSelect=0;
	$scope.listBarcode=[];
	$scope.palletEdit={};
	$scope.teamLixEdit=0;
	$scope.shiftLixEdit=0;
	/* tab1 */
	$scope.shiftLixPalletSelect=0;
	$scope.teamLixPalletSelect=0;
	$scope.numPallet=0;
	$scope.reached=true;
	$scope.listButton=[];
	$scope.listTeamLixPallet=[];
	$scope.listPalletLix=[];
	$scope.accountId=0;
	$scope.listPalletSelected=[];
	$scope.password="";
	$scope.actionEdit=false;
	$scope.tempPass="lixco2019";
	$scope.listProductAuto=[];
	$scope.workshopLixEdit=0;
	$scope.listTeamLixSelectEdit = [];
	$scope.productAutoComple=null;
	/* tab2 */
	$scope.workShopListSelectTab2=0;
	$scope.teamLixSelectTab2=0;
	$scope.shiftLixSelectTab2=0;
	$scope.listShiftLixTab2=[];
	$scope.dateStartShift="";
	$scope.listTeamSelectTab2=[];
	$scope.listPalletLixSumaryTab2=[];
	$scope.palletCreate={};
	$scope.resultList={};
	$scope.deliverySumary={};
	$scope.boolOff=true;
	
	$rootScope.listTongHop=[];
	$scope.formatStringShort=function(strss =""){
		var arr= strss.split(" ");
		return arr[0];
	}
	$scope.getFormattedDateShort = function(today) {
		var dd = today.getDate();
		var mm = today.getMonth() + 1; // January is 0!

		var yyyy = today.getFullYear();
		if (dd < 10) {
		  dd = '0' + dd;
		} 
		if (mm < 10) {
		  mm = '0' + mm;
		} 
		var today = dd + '/' + mm + '/' + yyyy;
	    return today;
	}
	$scope.changeIdentifyMachine = function(){
		localStorage.setItem("identify",$scope.identify+'');
	}
	$scope.init = function() {
		$scope.identify=localStorage.getItem("identify")
		var strListProductSy=localStorage.getItem('listProduct');
		if(strListProductSy == undefined || strListProductSy ==null || strListProductSy ==""){
			MyService.getListObjectWithParam("productlix", 'list', "{}", getDataProduct)
			function getDataProduct(response) {
				if(response.data.err==0){
				  localStorage.setItem('listProduct', JSON.stringify(response.data.dt.listproduct));
				}
			}
		}
		var strListShiftSy=localStorage.getItem('listShifLix');
		if(strListShiftSy == undefined || strListShiftSy == null || strListShiftSy == ""){
			MyService.getListObjectWithParam("shiftlix","list","{}",saveListShiftLixLocal);
			function saveListShiftLixLocal(response){
				if(response.data.err==0){
				   localStorage.setItem("listShifLix", JSON.stringify(response.data.dt.listShiftLix));
				   var strListShift=localStorage.getItem('listShifLix');
					if(strListShift !== undefined && strListShift !=null){
						$scope.listShiftLix=JSON.parse(strListShift);
						$scope.listShiftLixTab2=JSON.parse(strListShift);
						$scope.listShiftLixTab2.splice(0,0,{id:0,name:'--select--'});
						if($scope.listShiftLix.length>0){
							$scope.shiftLixSelect=$scope.listShiftLix[0].id;
							$scope.shiftLixPalletSelect=$scope.listShiftLix[0].id
						}
					}
				}
			}
		}else{
			$scope.listShiftLix=JSON.parse(strListShiftSy);
			$scope.listShiftLixTab2=JSON.parse(strListShiftSy);
			$scope.listShiftLixTab2.splice(0,0,{id:0,name:'--select--'});
			if($scope.listShiftLix.length>0){
				$scope.shiftLixSelect=$scope.listShiftLix[0].id;
				$scope.shiftLixPalletSelect=$scope.listShiftLix[0].id
			}
		}
		var strListWorkshopSy=localStorage.getItem('listWorkshopLix');
		if(strListWorkshopSy == undefined || strListWorkshopSy == null || strListWorkshopSy == ""){
			MyService.getListObjectWithParam("workshoplix","list","{}",saveListWorkshopLocal);
			function saveListWorkshopLocal(response){
				if(response.data.err==0){
					   localStorage.setItem("listWorkshopLix", JSON.stringify(response.data.dt.listWorkshopLix));
					   var strListWorkshop=localStorage.getItem('listWorkshopLix');
						if(strListWorkshop !== undefined && strListWorkshop !=null){
							$scope.listWorkshopLix=JSON.parse(strListWorkshop);
							$scope.listWorkshopLix.splice(0, 0, {id:0,name:'--select--'});
						}
			    }
			}
		}else{
			$scope.listWorkshopLix=JSON.parse(strListWorkshopSy);
			$scope.listWorkshopLix.splice(0, 0, {id:0,name:'--select--'});
		}
		var strListTeamLixSy= localStorage.getItem('listTeamLix');
		if(strListTeamLixSy == undefined || strListTeamLixSy == null || strListTeamLixSy == ""){
			MyService.getListObjectWithParam("teamlix","list","{}",saveListTeamLixLocal);
			function saveListTeamLixLocal(response){
				if(response.data.err==0){
					   localStorage.setItem("listTeamLix", JSON.stringify(response.data.dt.listTeamLix));
					   $scope.listTeamLixPallet=[];
						var strListTeamLix = localStorage.getItem('listTeamLix');
						if(strListTeamLix !== undefined && strListTeamLix !=null){
							$scope.listTeamLixPallet=JSON.parse(strListTeamLix);
							$scope.listTeamLixPallet.splice(0, 0, {id:0,name:'--select--'});
						}
			    }
			}
		}else{
			$scope.listTeamLixPallet=JSON.parse(strListTeamLixSy);
			$scope.listTeamLixPallet.splice(0, 0, {id:0,name:'--select--'});
		}
		$scope.listTeamLix=[];
		$scope.listTeamLix.push({id:0,name:'--select--'});
		$scope.listTeamSelectTab2=[];
		$scope.listTeamSelectTab2.push({id:0,name:'--select--'});
		$scope.getListProductionInCookie();
	}
	$scope.removeListInStore=function(id){
		var strData1=localStorage.getItem("listPalletLix");
		if(strData1 !== undefined && strData1 !=null && strData1 !=""){
			var listTempAdd=JSON.parse(strData1);
			for(i in listTempAdd){
				if(id==listTempAdd[i].id){
					listTempAdd.splice(i,1);
					localStorage.setItem("listPalletLix",JSON.stringify(listTempAdd));
					break;
				}
			}
		}
	}
	$scope.getListProductionInCookie=function(){
		var strListBarcode=localStorage.getItem('listBarcode');
		if(strListBarcode !== undefined && strListBarcode !=null && strListBarcode!="" && strListBarcode !="undefined"){
		  $scope.listBarcode=JSON.parse(strListBarcode);
		  if($scope.listBarcode !=null && $scope.listBarcode.length>0){
			  var d = new Date();
			  d.setDate(d.getDate()-1);
			  var strDateShort = $scope.getFormattedDateShort(d)+" 00:00:00";
			  var time=new Date(strDateShort.substring(3,6) + strDateShort.substring(0,2) + strDateShort.substring(5,strDateShort.length)).getTime();
			  var len=$scope.listBarcode.length;
			  while(len--){
				  var dateBC=$scope.listBarcode[len].date_add;
				  var timeItem=new Date(dateBC.substring(3,6) + dateBC.substring(0,2) + dateBC.substring(5,dateBC.length)).getTime();
				  if(timeItem < time){
					  $scope.listBarcode.splice(len,1);
				  }
			  }
			  localStorage.setItem("listBarcode",JSON.stringify($scope.listBarcode));
		  }
	    }else{
	    	$scope.listBarcode=[];
	    }
	}
	$scope.getListPalletOnlineInShift=function(){
		var str_identify=localStorage.getItem("identify");
		if(str_identify != undefined && str_identify !=null && str_identify !="" && str_identify !="undefined"){
			var iden={identify: str_identify};
			MyService.getListObjectWithParam("palletlix","list_pallet_in_shift",JSON.stringify(iden),saveListPalletLixLocal);
			function saveListPalletLixLocal(response){
				if(response.data.err==0){
				    localStorage.setItem("listPalletLix",JSON.stringify(response.data.dt.listpalletlix));
				}
			}
		}
	}
	$scope.asynDataSpecial=function(){
		let strData=localStorage.getItem("listPalletLix");
		let strStringD=localStorage.getItem("listDelivery");
		if(strStringD !== undefined && strStringD !=null && strStringD !="" && strStringD !="undefined"){
			let listm=JSON.parse(strStringD);
			let lobj={
					listInfo:listm
			}
			let strsumary=JSON.stringify(lobj);
			if(listm !==null  && listm.length >0){
				MyService.addObjectWithParam("palletlix","save_delivery",strsumary,syncDataDeliveryPalletOffline);
				function syncDataDeliveryPalletOffline(response){
					if(response.data.err==0){
						console.log("complete delivery....");
						let data=response.data.dt;
						let len=listm.length;
						var messages="";
						let listIdDeliveryErr=[];
						let listResultNumPalletProductErr=[];
						if(data !==undefined && data !=null){
							let listIdDeliverySuccess=data.list_delivery_id;
							listIdDeliveryErr=data.list_id_delivery_err;
							listResultNumPalletProductErr=data.list_result_num_pallet_product;
							if(listIdDeliverySuccess !==undefined && listIdDeliverySuccess!=null && listIdDeliverySuccess.length>0){
								messages+="- Danh sách phiếu giao nhận đồng bộ thành công có ID là: ";
								messages +="["+listIdDeliverySuccess.toString()+"]";
								while(len--){
									if(listIdDeliverySuccess.indexOf(listm[len].delivery.id)!=-1){
										listm.splice(len,1);
									}
								}
							    localStorage.setItem("listDelivery",JSON.stringify(listm));
							}
							if(listIdDeliveryErr !==undefined && listIdDeliveryErr !=null && listIdDeliveryErr.length>0){
								messages +="\n - Danh sách phiếu giao nhận bị lỗi vì chứa pallet đã tạo phiếu giao nhận có ID là:";
							    messages +="["+listIdDeliveryErr.toString()+"]";
							}
							if(listResultNumPalletProductErr !==undefined && listResultNumPalletProductErr !=null && listResultNumPalletProductErr.length>0){
								messages +="\n -Danh sách số thứ tự  pallet bị lỗi vì đã tạo phiếu giao nhận: ";
								let strIdentify=localStorage.getItem("identify");
								if(strIdentify === undefined || strIdentify ==null || strIdentify =="" || strIdentify =="undefined"){
									strIdentify="";
								}
								for(let i in listResultNumPalletProductErr){
									let list_none=listResultNumPalletProductErr[i].list_num_pallet_err;
									if(list_none !=undefined && list_none !=null && list_none.length>0){
										list_none=list_none.sort(function(a, b){return a-b});
										let string="";
										for(let i in list_none){
											string+=strIdentify+list_none[i]+",";
										}
										messages +="\n +"+listResultNumPalletProductErr[i].product_name+": ["+string+"]";
									}
								}
							}
							
						}else{
							document.getElementById("nupdongboid").disabled = false;
							swaldesigntimer('Thất bại!', 'Phiếu giao nhận có chứa pallet đã tạo phiếu giao nhận lúc online!','error',3000);
							return;
						}
						if(strData !== undefined && strData !=null && strData !="" && strData !="undefined"){
							let listPlletss=JSON.parse(strData);
							if(listPlletss !== null && listPlletss.length >0){
								MyService.addObjectWithParam("palletlix","save_pallet",JSON.stringify({listPalletLix:listPlletss}),syncDataListPalletOffline);
								function syncDataListPalletOffline(response){
									console.log(response);
									if(response.data.err==0){
										let arrayDelete=response.data.dt;
										if(arrayDelete !== undefined && arrayDelete !=null && arrayDelete.length >0){
											for(let ik in arrayDelete){
												$scope.removeListInStore(arrayDelete[ik]);
											}
										}
										$scope.getListPalletOnlineInShift();
										console.log("complete pallet.....");
										document.getElementById("nupdongboid").disabled = false;
										$scope.getListButton2();
										if(listIdDeliveryErr!==undefined && listIdDeliveryErr !=null && listIdDeliveryErr.length>0){
											swal({
											    title: "Thông báo",
											    text: messages,
											    type: "info",
											    confirmButtonColor: "#DD6B55",
											    confirmButtonText: "Đồng ý",
											    confirmButtonColor: '#3085d6'
											
											});
										}else{
											swaldesigntimer('Thành công!', 'đồng bộ thành công!','success',2000);
											
										}
									}
									$scope.duLieuOffline();
								}
							}else{
								$scope.getListPalletOnlineInShift();
								$scope.getListButton2();
								document.getElementById("nupdongboid").disabled = false;
								if(listIdDeliveryErr!==undefined && listIdDeliveryErr !=null && listIdDeliveryErr.length>0){
									swal({
									    title: "Thông báo",
									    text: messages,
									    type: "info",
									    confirmButtonColor: "#DD6B55",
									    confirmButtonText: "Đồng ý",
									    confirmButtonColor: '#3085d6'
									
									});
								}else{
									swaldesigntimer('Thành công!', 'đồng bộ thành công!','success',2000);
									
								}
								$scope.duLieuOffline();
							}
						}else{
							$scope.getListPalletOnlineInShift();
							$scope.getListButton2();
							document.getElementById("nupdongboid").disabled = false;
							if(listIdDeliveryErr!==undefined && listIdDeliveryErr !=null && listIdDeliveryErr.length>0){
								swal({
								    title: "Thông báo",
								    text: messages,
								    type: "info",
								    confirmButtonColor: "#DD6B55",
								    confirmButtonText: "Đồng ý",
								    confirmButtonColor: '#3085d6'
								
								});
							}else{
								swaldesigntimer('Thành công!', 'đồng bộ thành công!','success',2000);
								
							}
							$scope.duLieuOffline();
						}
					}
				}
		     }else{
		    	 // dong bo pallet
		    	 if(strData !== undefined && strData !=null && strData!="" && strData != "undefined"){
						let listPlletsss1=JSON.parse(strData);
						if(listPlletsss1 !== null && listPlletsss1.length >0){
							MyService.addObjectWithParam("palletlix","save_pallet",JSON.stringify({listPalletLix:listPlletsss1}),syncDataListPalletOffline12);
							function syncDataListPalletOffline12(response){
								if(response.data.err==0){
									let arrayDelete1=response.data.dt;
									if(arrayDelete1 !== undefined && arrayDelete1 !=null && arrayDelete1.length >0){
										for(ik1 in arrayDelete1){
											$scope.removeListInStore(arrayDelete1[ik1]);
										}
									}
									$scope.getListPalletOnlineInShift();
									console.log("complete pallet no delivery.....");
									swaldesigntimer('Thành công!', 'đồng bộ thành công!','success',2000);
									document.getElementById("nupdongboid").disabled = false;
									$scope.getListButton2();
									$scope.duLieuOffline();
								}
							}
					   }else{
						   $scope.getListPalletOnlineInShift();
						   $scope.getListButton2();
						   document.getElementById("nupdongboid").disabled = false;
						   swaldesigntimer('Thành công!', 'đồng bộ thành công!','success',2000);
						   $scope.duLieuOffline();
					   }
					}else{
						$scope.getListPalletOnlineInShift();
						$scope.getListButton2();
						document.getElementById("nupdongboid").disabled = false;
						swaldesigntimer('Thành công!', 'đồng bộ thành công!','success',2000);
						$scope.duLieuOffline();
					}
		     }
		}else{
			let strData=localStorage.getItem("listPalletLix");
			if(strData != "undefined" && strData !== undefined && strData !=null){
				let listPlletss=JSON.parse(strData);
				if(listPlletss != null && listPlletss.length >0){
				    MyService.addObjectWithParam("palletlix","save_pallet",JSON.stringify({listPalletLix:listPlletss}),syncDataListPalletOffline);
					function syncDataListPalletOffline(response){
						console.log(response);
						if(response.data.err==0){
							let arrayDelete=response.data.dt;
							if(arrayDelete !== undefined && arrayDelete !=null && arrayDelete.length >0){
								for(let ik in arrayDelete){
									$scope.removeListInStore(arrayDelete[ik]);
								}
							}
							swaldesigntimer('Thành công!', 'Đồng bộ thành công!','success',2000);
							document.getElementById("nupdongboid").disabled = false;
							$scope.getListPalletOnlineInShift();
							$scope.getListButton2();
							$scope.duLieuOffline();
						}
					}
			    }else{
			    	document.getElementById("nupdongboid").disabled = false;
			    	$scope.getListPalletOnlineInShift();
			    	$scope.getListButton2();
			    	$scope.duLieuOffline();
			    }
			}else{
				$scope.getListPalletOnlineInShift();
				$scope.getListButton2();
				document.getElementById("nupdongboid").disabled = false;
				$scope.duLieuOffline();
			}
		}
		
	}
	$scope.init();

	$scope.changeNumberRound=function(value){
		return  Math.round(value * 1000)/1000;
	}
	
	$scope.actionButtonProductClick=function(item,$event){
		$($event.currentTarget).attr("disabled", "disabled");
		let dinhdanh=localStorage.getItem("identify");
		if(dinhdanh !== undefined && dinhdanh !=null && dinhdanh !="" && dinhdanh !="undefined"){
			if(item !== undefined && item !=null){
				let barcode = item.barcode.replace(/\*/g, '');
				let masp = barcode.substring(0, 5);
				let maca = new Number(barcode.substring(5, 6));
				let mato = new Number(barcode.substring(6,8));
				let masps = masp;
				for (let i = 0; i < masp.length; i++) {
					if (masp.charAt(i) != '0') {
						break;
					}
					masps = masps.substring(1);
				}
				let product=$scope.getProductLix(masps);
				let shift=$scope.getShiftLix(maca);
				let team=$scope.getTeamLix(mato);
				Number.prototype.padLeft = function(base,chr){
					   var  len = (String(base || 10).length - String(this).length)+1;
					   return len > 0? new Array(len).join(chr || '0')+this : this;
				}
				 let d = new Date,
			        dformat = [ d.getDate().padLeft(),
			        	       (d.getMonth()+1).padLeft(),
			                    d.getFullYear()].join('/')+
			                    ' ' +
			                  [ d.getHours().padLeft(),
			                    d.getMinutes().padLeft(),
			                    d.getSeconds().padLeft()].join(':');
				let nummpl=product.num_pallet;
				if($scope.numPallet !=0 && $scope.numPallet !='' && $scope.numPallet !=null){
					nummpl=$scope.numPallet;
				}
				
				let curr=item.cur_pall;
				curr=curr+1; 
				$scope.palletCreate={
						id:0,
						createdDate:dformat,
						teamLix:team,
						date_start_shift:'',
						shiftLix:shift,
						reached:$scope.reached,
						num_order:curr,
						identify:dinhdanh,
						productLix:product,
						num_pallet:nummpl
				}
				let datecurr1=new Date();
				let str_datecurr=$scope.getFormattedDateShort(datecurr1);
				if(shift.shift_num==1){
					
					$scope.palletCreate.date_start_shift=str_datecurr+" 00:00:00";
				}else{
					let target=" 08:00:00";
					let strDateTarget=str_datecurr+target;
					let dateTarget=new Date(strDateTarget.substring(3,6) + strDateTarget.substring(0,2) + strDateTarget.substring(5,strDateTarget.length));
					if(datecurr1.getTime() < dateTarget.getTime()){// qua 24h
						datecurr1.setDate(datecurr1.getDate()-1);
					}
					$scope.palletCreate.date_start_shift=$scope.getFormattedDateShort(datecurr1)+" 00:00:00";
				}
				item.cur_pall=curr;
				let listPllet=[];
				let strListPalletLix=localStorage.getItem('listPalletLix');
				if(strListPalletLix !== undefined && strListPalletLix !=null && strListPalletLix !="" && strListPalletLix !="undefined"){
					listPllet=JSON.parse(strListPalletLix);
					listPllet.push($scope.palletCreate);
					$scope.listPalletLix=[]
					let minid=0;
					for(index in listPllet){
						let idpallet=listPllet[index].id;
						if(idpallet < minid){
							minid=idpallet;
						}
						let idT=listPllet[index].teamLix.id;
						let idS=listPllet[index].shiftLix.id;
						if($scope.shiftLixPalletSelect==idS && $scope.teamLixPalletSelect==idT && dinhdanh==listPllet[index].identify){
							$scope.listPalletLix.push(listPllet[index]);
						}
					}
					$scope.palletCreate.id=minid-1;
					localStorage.setItem("listPalletLix",JSON.stringify(listPllet));
				}else{
					listPllet.push($scope.palletCreate);
					localStorage.setItem("listPalletLix",JSON.stringify(listPllet));
				}
				localStorage.setItem("listBarcode", JSON.stringify($scope.listBarcode));
	            $scope.pdf();
				setTimeout('$(".lllserver").removeAttr("disabled")', 1000);
				
			}
		}else{
			swaldesigntimer('Thất bại', 'Định danh máy chưa được cài đặt!','error',2000);
		}
		$scope.kiemTraKetNoi();
		console.log("sssss")
	}
	
	$scope.printDelivery=function(){
		setTimeout(function () {
	           var contents = $("#deliveryID").html();
	           var frame1 = document.createElement('iframe');
	           frame1.name = "frame1";
	           frame1.style.position = "absolute";
	           frame1.style.top = "-1000000px";
	           document.body.appendChild(frame1);
	           var frameDoc = frame1.contentWindow ? frame1.contentWindow : frame1.contentDocument.document ? frame1.contentDocument.document : frame1.contentDocument;
	           frameDoc.document.open();
	           frameDoc.document.write('<html><head><title>&nbsp</title><style type="text/css" media="print">@page{size:  auto; margin: 10mm 3mm 0mm 3mm; }html{ margin:10px 3px 0px 3px;}body{margin: 0mm 5mm 0mm 5mm; }</style>');

	           frameDoc.document.write('</head><body>');
	           frameDoc.document.write(contents);
	           frameDoc.document.write('</body></html>');
	           frameDoc.document.close();
	           console.log($scope.palletCreate)
	           window.frames["frame1"].focus();
	           window.frames["frame1"].print();
	           document.body.removeChild(frame1);
			},0);
	        return false;
	}
	$scope.pdf = function () {
		setTimeout(function () {
           var contents = $("#bill").html();
           var frame1 = document.createElement('iframe');
           frame1.name = "frame1";
           frame1.style.position = "absolute";
           frame1.style.top = "-1000000px";
           document.body.appendChild(frame1);
           var frameDoc = frame1.contentWindow ? frame1.contentWindow : frame1.contentDocument.document ? frame1.contentDocument.document : frame1.contentDocument;
           frameDoc.document.open();
           frameDoc.document.write('<html><head><title>&nbsp</title><style type="text/css" media="print">@page{size:  auto; margin: 10mm 3mm 0mm 3mm; }html{ margin:10px 3px 0px 3px;}body{margin: 0mm 5mm 0mm 5mm; }</style>');

           frameDoc.document.write('</head><body>');
           frameDoc.document.write(contents);
           frameDoc.document.write('</body></html>');
           frameDoc.document.close();
           console.log($scope.palletCreate)
           window.frames["frame1"].focus();
           window.frames["frame1"].print();
           document.body.removeChild(frame1);
		},0);
        return false;
    }
	$scope.buttonEditClick=function(){
		if($scope.listPalletSelected !=null && $scope.listPalletSelected !=undefined && $scope.listPalletSelected.length >0){
		  $scope.actionEdit=true;
		  q$('#myModal1').modal({
				backdrop: 'static'
			});
		}else{
			swaldesigntimer('Thất bại', 'Chưa chọn một dòng để chỉnh sửa!','error',2000);
		}
		$scope.kiemTraKetNoi();
	}
	$scope.deleteClick=function(){
		if($scope.listPalletSelected !=null && $scope.listPalletSelected !=undefined && $scope.listPalletSelected.length >0){
		    $scope.actionEdit=false;
		    q$('#myModal1').modal({
				backdrop: 'static'
			});
		}else{
			swaldesigntimer('Thất bại', 'Chưa Chọn một dòng để xóa!','error',2000);
		}
		$scope.kiemTraKetNoi();
	}
	$scope.editPallet=function(){
		$scope.palletEdit={};
		$scope.palletEdit = Object.assign({}, $scope.listPalletSelected[0]);
		q$('#myModal2').modal({
			backdrop: 'static'
		});
		$scope.workshopLixEdit=$scope.listPalletSelected[0].teamLix.workshopLix.id;
		$scope.teamLixEdit=$scope.listPalletSelected[0].teamLix.id;
	    $scope.shiftLixEdit=$scope.listPalletSelected[0].shiftLix.id;
	    $scope.productCompleteNew=$scope.listPalletSelected[0].productLix;
	    $scope.productBind=$scope.listPalletSelected[0].productLix.name;
		$scope.changeWorkshopLixEdit();

		q$("#myModal1").modal("hide");
		$scope.password="";
	}
	$scope.savePalletEdit=function(){
		if($scope.palletEdit !=null && $scope.palletEdit !=undefined ){
			  let createDate =document.getElementById("datetimepicker").value;
			  if($scope.palletEdit.num_pallet ===undefined || $scope.palletEdit.num_pallet == null ||  $scope.palletEdit.num_pallet == "" || $scope.palletEdit.num_pallet =="undefined"){
				  $scope.palletEdit.num_pallet=0
			  }
			  if(createDate !=undefined && createDate != null && createDate !="" && $scope.productCompleteNew != undefined &&
			    $scope.productCompleteNew !=null && $scope.shiftLixEdit !=0 && $scope.teamLixEdit !=0){
				  let strListPalletLix=localStorage.getItem('listPalletLix');
				  let teamLixT=$scope.getTeamLix($scope.teamLixEdit);
				  let shiftLixT=$scope.getShiftLix($scope.shiftLixEdit);
					if(strListPalletLix !== undefined && strListPalletLix !=null && strListPalletLix !="" && strListPalletLix !="undefined"){
						let listTemp=JSON.parse(strListPalletLix);
						for(let index in listTemp){
							if(listTemp[index].id == $scope.palletEdit.id){
								$scope.palletEdit.teamLix=teamLixT;
								$scope.palletEdit.shiftLix=shiftLixT;
								$scope.palletEdit.productLix=$scope.productCompleteNew;
								$scope.palletEdit.createdDate = createDate;
								$scope.palletEdit.editp=true;
								listTemp[index]= $scope.palletEdit;
								localStorage.setItem("listPalletLix",JSON.stringify(listTemp));
								$scope.listPalletSelected[0]=$scope.palletEdit;
								swaldesigntimer('Thành công', 'Chỉnh sửa thành công!','success',2000);
								$scope.getListButton();
								break;
							}
						}
						
					}else{
						swaldesigntimer('Thất bại', 'Lỗi hệ thống!','error',2000);
					}
			  }else{
				  swaldesigntimer('Thất bại', 'Thông tin không đầy đủ!','error',2000);
			  }
				
		}
	}
	$scope.selectedProductAuto=null;
	$scope.localSearch = function(str, listProductAuto) {
		$scope.listProductAuto = [];
	    var strListProduct=localStorage.getItem('listProduct');
		if(strListProduct !== undefined && strListProduct!=null){
			var listT=JSON.parse(strListProduct);
			var i=0;
			str=str.toLowerCase();
			listT.forEach(function(product){
				var code=product.product_code.toLowerCase();
				var name=product.name.toLowerCase();
				if(i==4){
					return $scope.listProductAuto;
				}
			    if((code.indexOf(str)>-1) || (name.indexOf(str)>-1)){
			    	$scope.listProductAuto.push(product);
			    	i++;
			    }
			});
		}
		return $scope.listProductAuto;
    };
	$scope.deletePallet=function(){
		var idCurr=$scope.listPalletSelected[0].id;
		if(idCurr <= 0){
			var maxcurr=$scope.listPalletSelected[0].num_orderoff;
			var product_id=$scope.listPalletSelected[0].productLix.id;
			var shift_id=$scope.listPalletSelected[0].shiftLix.id;
			var team_id=$scope.listPalletSelected[0].teamLix.id;
			var strListPalletLix=localStorage.getItem('listPalletLix');
			if(strListPalletLix !== undefined && strListPalletLix !=null && strListPalletLix != "undefined"){
				var listTemp=JSON.parse(strListPalletLix);
				for(index1 in listTemp){
					var ptemp_id=listTemp[index1].productLix.id;
					var stemp_id=listTemp[index1].shiftLix.id;
					var ttemp_id=listTemp[index1].teamLix.id;
					var maxtemp=listTemp[index1].num_orderoff;
					if(product_id==ptemp_id && shift_id==stemp_id && team_id==ttemp_id){
						if(maxcurr < maxtemp){
							swaldesigntimer('Thất bại', 'Chỉ được xóa pallet cuối cùng !','error',2000);
							return;
						}
					}
				}
				for(index in listTemp){
					var id=listTemp[index].id;
					if(idCurr == id){
						listTemp.splice(index, 1);
						localStorage.setItem("listPalletLix",JSON.stringify(listTemp));
						swaldesigntimer('Thành công', 'Xóa thành công!','success',2000);
						break;
					}
				}
				// cập nhật lại số thứ tự trên nút bấm pallet.
				var strListBarcode=localStorage.getItem('listBarcode');
				if(strListBarcode !== undefined && strListBarcode !=null && strListBarcode !="undefined"){
				  var listBarcode=JSON.parse(strListBarcode);
				  for(pj in listBarcode){
				    var barcode = listBarcode[pj].barcode.replace(/\*/g, '');
					var maca = new Number(barcode.substring(5, 6));
					var mato = new Number(barcode.substring(6));
					var masp = barcode.substring(0, 5);
					var masps = masp;
					for (i = 0; i < masp.length; i++) {
						if (masp.charAt(i) != '0') {
							break;
						}
						masps = masps.substring(1);
					}
					var productt=$scope.getProductLix(masps);
					if(shift_id==maca &&  team_id==mato && productt.product_code == $scope.listPalletSelected[0].productLix.product_code){
						listBarcode[pj].cur_pall=listBarcode[pj].cur_pall-1;
						localStorage.setItem('listBarcode',JSON.stringify(listBarcode));
						break;
					}
				  }
				}
				$scope.getListButton();
			}
			q$("#myModal1").modal("hide");
			$scope.password="";
	   }else{
		   swaldesigntimer('Thất bại', 'Pallet này đã được đồng độ về server không được xóa!','error',2000);
	   }
	}
	$scope.rePrintPallet=function(){
		 if($scope.listPalletSelected !==undefined && $scope.listPalletSelected !=null &&  $scope.listPalletSelected.length>0){
			 $scope.palletCreate=$scope.listPalletSelected[0];
			 $scope.pdf();
		 }
		 $scope.kiemTraKetNoi();
		 console.log("sssss")
	}
	$scope.authenication=function(){
		if($scope.password==$scope.tempPass){
			if($scope.actionEdit){
				$scope.editPallet();
			}else{
				$scope.deletePallet();
			}
		}else{
			swaldesigntimer('Xác thực', 'Mật khẩu vừa nhập không chính xác xin thử lại!','error',2000);
		}
	}
	$scope.palletClick=function(id,pallet){
		$("#mytable1 tr").removeClass("clickedtt");
        $("#pallet_" + id).addClass("clickedtt");
        $scope.listPalletSelected = [];
        $scope.listPalletSelected.push(pallet);
	}
	$scope.getListButton=function(){
		$scope.listButton=[];
		$scope.getListProductionInCookie();
		let identifyMac=localStorage.getItem('identify');
		if(identifyMac !== undefined && identifyMac !=null && identifyMac != "" && identifyMac !="undefined"){
			if($scope.shiftLixPalletSelect !=0 && $scope.teamLixPalletSelect !=0){
				let dateCurrent=new Date();
				let ngayca=new Date();
				let shift=$scope.getShiftLix($scope.shiftLixPalletSelect);
				if(shift.shift_num==1){
					let strDateShort=$scope.getFormattedDateShort(dateCurrent)+" 00:00:00";
					ngayca=new Date(strDateShort.substring(3,6) + strDateShort.substring(0,2) + strDateShort.substring(5,strDateShort.length));
				}else if(shift.shift_num==2){
					let target=" 08:00:00";
					let strDateTarget=$scope.getFormattedDateShort(dateCurrent)+target;
					let dateTarget=new Date(strDateTarget.substring(3,6) + strDateTarget.substring(0,2) + strDateTarget.substring(5,strDateTarget.length));
					if(dateCurrent.getTime() < dateTarget.getTime()){
						dateCurrent.setDate(dateCurrent.getDate()-1);
						
					}
					let strMinus1Date=$scope.getFormattedDateShort(dateCurrent)+" 00:00:00";
					ngayca=new Date(strMinus1Date.substring(3,6) + strMinus1Date.substring(0,2) + strMinus1Date.substring(5,strMinus1Date.length));
				}else{
					// truong hop sau nay phat ssinh ca 3
					let strDate3=$scope.getFormattedDateShort(dateCurrent)+" 00:00:00";
					ngayca=new Date(strDate3.substring(3,6) + strDate3.substring(0,2) + strDate3.substring(5,strDate3.length));
				}
				for(i=0;i<$scope.listBarcode.length;i++){
					let barcode = $scope.listBarcode[i].barcode.replace(/\*/g, '');
					let maca = new Number(barcode.substring(5, 6));
					let mato = new Number(barcode.substring(6));
					let strNgayCaInBC=$scope.listBarcode[i].date_add;
					let dateBC=new Date(strNgayCaInBC.substring(3,6) + strNgayCaInBC.substring(0,2) + strNgayCaInBC.substring(5,strNgayCaInBC.length));
					if($scope.shiftLixPalletSelect==maca && $scope.teamLixPalletSelect==mato && dateBC.getTime()== ngayca.getTime()){
						$scope.listButton.push($scope.listBarcode[i]);
					}
				}
				$scope.listPalletLix=[]
				let listTemp=[];
				let strListPalletLix=localStorage.getItem('listPalletLix');
				if(strListPalletLix !== undefined && strListPalletLix !=null && strListPalletLix !="undefined"){
					listTemp=JSON.parse(strListPalletLix);
				}
				for(let index in listTemp){
					let idT=listTemp[index].teamLix.id;
					let idS=listTemp[index].shiftLix.id;
					if($scope.shiftLixPalletSelect==idS && $scope.teamLixPalletSelect==idT && listTemp[index].identify==identifyMac){
						$scope.listPalletLix.push(listTemp[index]);
					}
				}
				
			}else{
				$scope.listPalletLix=[];
			}
			$scope.kiemTraKetNoi();
			console.log("sssss")
			return false;
	  }else{
		  swaldesigntimer('Thất bại', 'Máy chưa cài định danh!','error',2000);
	  }
	}
	$scope.getListButton2=function(){
		$scope.listButton=[];
		$scope.getListProductionInCookie();
		let identifyMac=localStorage.getItem('identify');
		if(identifyMac !== undefined && identifyMac !=null && identifyMac != "" && identifyMac !="undefined"){
			if($scope.shiftLixPalletSelect !=0 && $scope.teamLixPalletSelect !=0){
				let dateCurrent=new Date();
				let ngayca=new Date();
				let shift=$scope.getShiftLix($scope.shiftLixPalletSelect);
				if(shift.shift_num==1){
					let strDateShort=$scope.getFormattedDateShort(dateCurrent)+" 00:00:00";
					ngayca=new Date(strDateShort.substring(3,6) + strDateShort.substring(0,2) + strDateShort.substring(5,strDateShort.length));
				}else if(shift.shift_num==2){
					let target=" 08:00:00";
					let strDateTarget=$scope.getFormattedDateShort(dateCurrent)+target;
					let dateTarget=new Date(strDateTarget.substring(3,6) + strDateTarget.substring(0,2) + strDateTarget.substring(5,strDateTarget.length));
					if(dateCurrent.getTime() < dateTarget.getTime()){
						dateCurrent.setDate(dateCurrent.getDate()-1);
						
					}
					let strMinus1Date=$scope.getFormattedDateShort(dateCurrent)+" 00:00:00";
					ngayca=new Date(strMinus1Date.substring(3,6) + strMinus1Date.substring(0,2) + strMinus1Date.substring(5,strMinus1Date.length));
				}else{
					// truong hop sau nay phat ssinh ca 3
					let strDate3=$scope.getFormattedDateShort(dateCurrent)+" 00:00:00";
					ngayca=new Date(strDate3.substring(3,6) + strDate3.substring(0,2) + strDate3.substring(5,strDate3.length));
				}
				for(i=0;i<$scope.listBarcode.length;i++){
					let barcode = $scope.listBarcode[i].barcode.replace(/\*/g, '');
					let maca = new Number(barcode.substring(5, 6));
					let mato = new Number(barcode.substring(6));
					let strNgayCaInBC=$scope.listBarcode[i].date_add;
					let dateBC=new Date(strNgayCaInBC.substring(3,6) + strNgayCaInBC.substring(0,2) + strNgayCaInBC.substring(5,strNgayCaInBC.length));
					if($scope.shiftLixPalletSelect==maca && $scope.teamLixPalletSelect==mato && dateBC.getTime()== ngayca.getTime()){
						$scope.listButton.push($scope.listBarcode[i]);
					}
				}
				$scope.listPalletLix=[]
				let listTemp=[];
				let strListPalletLix=localStorage.getItem('listPalletLix');
				if(strListPalletLix !== undefined && strListPalletLix !=null && strListPalletLix !="undefined"){
					listTemp=JSON.parse(strListPalletLix);
				}
				for(let index in listTemp){
					let idT=listTemp[index].teamLix.id;
					let idS=listTemp[index].shiftLix.id;
					if($scope.shiftLixPalletSelect==idS && $scope.teamLixPalletSelect==idT && listTemp[index].identify==identifyMac){
						$scope.listPalletLix.push(listTemp[index]);
					}
				}
				
			}else{
				$scope.listPalletLix=[];
			}
			return false;
		}else{
			swaldesigntimer('Thất bại', 'Máy chưa cài định danh!','error',2000);
		}
	}
	$scope.indentifyNumpallet=function(pcode,tId,sId){
		let listTemp=[]
		let strListPalletLix=localStorage.getItem('listPalletLix');
		if(strListPalletLix !== undefined && strListPalletLix !=null && strListPalletLix !="" && strListPalletLix !="undefined"){
			listTemp=JSON.parse(strListPalletLix);
		}
		let strldate='12/11/1992 00:00:00';
		let readableDate1 = strldate.substring(3,6) + strldate.substring(0,2) + strldate.substring(5,strldate.length);
		let dateMax=new Date(readableDate1);
		let tractPallet=null;
		let shift=$scope.getShiftLix(sId);
		let listStand=[];
		for(let index in listTemp){
			let codeStand=listTemp[index].productLix.product_code;
			let idTeamStand=listTemp[index].teamLix.id;
			let idShiftStand=listTemp[index].shiftLix.id;
			
			if(codeStand==pcode && idTeamStand==tId && idShiftStand==sId){
				listStand.push(listTemp[index]);
			}
		}
		if(listStand.length>0){
			let listRealPallet_=[]
			let currentDate=new Date();
			let strCurrDate=$scope.getFormattedDateShort(currentDate);
			if(shift.shift_num==1){
				let strdatestartshift=strCurrDate+" 00:00:00";
				let datestartshift= new Date(strdatestartshift.substring(3,6) + strdatestartshift.substring(0,2) + strdatestartshift.substring(5,strdatestartshift.length));
				let timedatestartshift=datestartshift.getTime();
				for(let index in listStand){
					let itemTime=listStand[index].date_start_shift;
					let dateItem=new Date(itemTime.substring(3,6) + itemTime.substring(0,2) + itemTime.substring(5,itemTime.length))
					let timeDateItem=dateItem.getTime();
					if(timedatestartshift==timeDateItem){
						listRealPallet_.push(listStand[index]);
					}
				}
			}else{
				let maxStrTS1=strCurrDate+" 08:00:00";
				let maxTimeS1=new Date(maxStrTS1.substring(3,6) + maxStrTS1.substring(0,2) + maxStrTS1.substring(5,maxStrTS1.length));
				let timeS1=maxTimeS1.getTime();
				let timePallet=currentDate.getTime();
				let strCompareVsPallet=strCurrDate+" 00:00:00";
				let timeCompareVsPallet=new Date(strCompareVsPallet.substring(3,6) + strCompareVsPallet.substring(0,2) + strCompareVsPallet.substring(5,strCompareVsPallet.length)).getTime();
				if(timePallet < timeS1){
					let d=currentDate;
					d.setDate(d.getDate()-1);
					let strd = $scope.getFormattedDateShort(d)+" 00:00:00";
					timeCompareVsPallet=new Date(strd.substring(3,6) + strd.substring(0,2) + strd.substring(5,strd.length)).getTime();
				}
				for(let i in listStand){
					let itemTime1=listStand[i].date_start_shift;
					let dateItem1=new Date(itemTime1.substring(3,6) + itemTime1.substring(0,2) + itemTime1.substring(5,itemTime1.length))
					let timeDateItem1=dateItem1.getTime();
					if(timeCompareVsPallet==timeDateItem1){
						listRealPallet_.push(listStand[i]);
					}
				}
			}
			if(listRealPallet_.length>0){
				let max=0;
				for(let k in listRealPallet_){
					if(listRealPallet_[k].num_order>max){
						max=listRealPallet_[k].num_order;
					}
				}
				return max;
			}
		}
		return 0;
	
	}
	
// console.log($scope.indentifyNumpallet('MM026',1,1));
	$scope.productInBarcodeClick=function(id,item){
		$("#mytable tr").removeClass("clickedtt");
        $("#productbc_" + id).addClass("clickedtt");
        $scope.productBCSelected = [];
        $scope.productBCSelected.push(item);
	}
	$scope.productInBarcodeDBClick=function(item){
		 $scope.productBCSelected = [];
         $scope.productBCSelected.push(item);
	}
	$scope.changeWorkShopBC=function(){
		$scope.listTeamLix=[];
		$scope.listTeamLix.push({id:0,name:'--select--'});
		if($scope.workshopLixSelect !== undefined && $scope.workshopLixSelect!=null && $scope.workshopLixSelect !=0){
			var strLisT=localStorage.getItem('listTeamLix');
			if(strLisT!==undefined && strLisT!=null){
				var listT=JSON.parse(strLisT);
				for(i=0;i<listT.length;i++){
					var id=listT[i].workshopLix.id;
					if(id==$scope.workshopLixSelect){
						$scope.listTeamLix.push(listT[i]);
					}
				}
			}
		}
	}
	
	$scope.changeWorkShopTab2=function(){
		$scope.listTeamSelectTab2=[];
		$scope.listTeamSelectTab2.push({id:0,name:'--select--'})
		if($scope.workShopListSelectTab2 !== undefined && $scope.workShopListSelectTab2 !=null && $scope.workShopListSelectTab2 !=0){
			var strLisT=localStorage.getItem('listTeamLix');
			if(strLisT!==undefined && strLisT!=null){
				var listT=JSON.parse(strLisT);
				for(i=0;i<listT.length;i++){
					var id=listT[i].workshopLix.id;
					if(id==$scope.workShopListSelectTab2){
						$scope.listTeamSelectTab2.push(listT[i]);
					}
				}
			}
		}
		$scope.kiemTraKetNoi();
		console.log("sssss")
	}
	$scope.changeWorkshopLixEdit=function(){
		$scope.listTeamLixSelectEdit=[];
		$scope.listTeamLixSelectEdit.push({id:0,name:'--select--'})
		if($scope.workshopLixEdit !== undefined && $scope.workshopLixEdit !=null && $scope.workshopLixEdit !=0){
			var strLisT=localStorage.getItem('listTeamLix');
			if(strLisT!==undefined && strLisT!=null){
				var listT=JSON.parse(strLisT);
				for(i=0;i<listT.length;i++){
					var id=listT[i].workshopLix.id;
					if(id==$scope.workshopLixEdit){
						$scope.listTeamLixSelectEdit.push(listT[i]);
					}
				}
			}
		}
	}
	$scope.deleteListBarcode=function(){
		localStorage.setItem("listBarcode", JSON.stringify([]));
		$scope.listBarcode=[];
		$scope.getListButton();
	}
	$scope.addProductInShift=function(){
		if($scope.shiftLixSelect==0){
			swaldesigntimer('Thất bại', 'Chưa chọn ca sản xuất!','error',2000);
		}else if($scope.teamLixSelect==0){
			swaldesigntimer('Thất bại', 'Chưa chọn tổ sản xuất!','error',2000);
		}else if($scope.productBCSelected.length==0){
			swaldesigntimer('Thất bại', 'Chưa chọn sản phẩm cần sản xuất!','error',2000);
		}else{
			let strListBarcode=localStorage.getItem('listBarcode');
			if(strListBarcode===undefined || strListBarcode==null || strListBarcode =='' || strListBarcode == "undefined"){
				localStorage.setItem("listBarcode", JSON.stringify([]));
				strListBarcode=JSON.stringify([])
			}
			$scope.listBarcode=JSON.parse(strListBarcode);
			let objShiftLix=$scope.getShiftLix($scope.shiftLixSelect);
			let objTeamLix=$scope.getTeamLix($scope.teamLixSelect);
			let objProduct=$scope.productBCSelected[0];
			let maca=objShiftLix.id;
			let masp=objProduct.product_code;
			let mato=objTeamLix.id;
			let mspx=masp;
			if(masp.length < 5){
				let zeropalce=5 - masp.length;
				let zerostr="";
				for(i=0;i<zeropalce;i++){
					zerostr +="0";
				}
				mspx=zerostr+masp;
			}
			let strmato= (mato < 10) ? '0'+ mato.toString() : mato+"";
			let barcode="*"+mspx + maca +strmato+"*";
			// xac dinh ngay ca trong barcode
			let dateCurrent=new Date();
			let ngayca=new Date();
			if(objShiftLix.shift_num==1){
				let strDateShort=$scope.getFormattedDateShort(dateCurrent)+" 00:00:00";
				ngayca=new Date(strDateShort.substring(3,6) + strDateShort.substring(0,2) + strDateShort.substring(5,strDateShort.length));
			}else if(objShiftLix.shift_num==2){
				let target=" 08:00:00";
				let strDateTarget=$scope.getFormattedDateShort(dateCurrent)+target;
				let dateTarget=new Date(strDateTarget.substring(3,6) + strDateTarget.substring(0,2) + strDateTarget.substring(5,strDateTarget.length));
				if(dateCurrent.getTime() < dateTarget.getTime()){
					dateCurrent.setDate(dateCurrent.getDate()-1);
					
				}
				let strMinus1Date=$scope.getFormattedDateShort(dateCurrent)+" 00:00:00";
				ngayca=new Date(strMinus1Date.substring(3,6) + strMinus1Date.substring(0,2) + strMinus1Date.substring(5,strMinus1Date.length));
			}else{
				// truong hop sau nay phat ssinh ca 3
				let strDate3=$scope.getFormattedDateShort(dateCurrent)+" 00:00:00";
				ngayca=new Date(strDate3.substring(3,6) + strDate3.substring(0,2) + strDate3.substring(5,strDate3.length));
			}
			
			let date_strcurr=$scope.getFormattedDateShort(ngayca)+" 00:00:00";
			let obj={
				barcode	:barcode,
				codep:objProduct.product_code,
				date_add:date_strcurr,
			    shift : objShiftLix.name,
			    product :objProduct.name,
			    team : objTeamLix.name,
			    gam  : (objProduct.kgbox * 1000),
			    cur_pall:$scope.indentifyNumpallet(masp,mato,maca)
			}
			for(let i=0;i<$scope.listBarcode.length;i++){
				let barcode=$scope.listBarcode[i].barcode;
				if(barcode==obj.barcode){
					swaldesigntimer('Thất bại', 'Sản xuất đã được thêm vào trước đó!','error',2000);
					return;
				}
			}
			$scope.listBarcode.push(obj);
			localStorage.setItem("listBarcode", JSON.stringify($scope.listBarcode));
			$scope.getListButton();
		}
	}
	$scope.getShiftLix=function(id){
		if(id==0){
			return null;
		}else{
			var strListShift=localStorage.getItem('listShifLix');
			if(strListShift !== undefined && strListShift !=null){
				var list=JSON.parse(strListShift);
				for(i=0;i<list.length;i++){
					if(list[i].id==id){
						return list[i];
					}
				}
				return null;
			}else{
				return null;
			}
		}
	}
	$scope.getTeamLix=function(id){
		if(id==0){
			return null;
		}
		else{
			var strLisT=localStorage.getItem('listTeamLix');
			if(strLisT!==undefined && strLisT!=null){
				var listT=JSON.parse(strLisT);
				for(i=0;i<listT.length;i++){
					if(listT[i].id==id){
						return listT[i];
					}
				}
				return null;
			}else{
				return null;
			}
		}
	}
	$scope.getProductLix=function(code){
		if(code == null || code === undefined){
			return null;
		}else{
			var strListProduct=localStorage.getItem('listProduct');
			if(strListProduct !== undefined && strListProduct!=null){
				var listT=JSON.parse(strListProduct);
				for(i=0;i<listT.length;i++){
					if(listT[i].product_code==code){
						return listT[i];
					}
				}
				return null;
			}else{
				return null;
			}
		}
	}
// $window.addEventListener("offline", function() {
// $rootScope.$apply(function() {
// $rootScope.online = false;
// document.getElementById("contentOnline").style.display = "none";
// document.getElementById("contentOffline").style.display = "block";
// });
// }, false);
	$scope.searProduct=function(){
		if ($.fn.dataTable.isDataTable( '#mytable' )) {
		    table = $('#mytable').DataTable();
		}
		else {
		    table = $('#mytable').DataTable( {
		        paging: false,
		        scrollY:500
		    } );
		}
		var list=JSON.parse(localStorage.getItem('listProduct'));
		var listTemp=[];
		for(i=0;i<list.length;i++){
			if(listTemp.length>50){
				$scope.listProduct=listTemp;
				return;
			}
			var code=""+list[i].product_code.toLowerCase().replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a").replace(/\ /g, '-').replace(/đ/g, "d").replace(/đ/g, "d").replace(/ỳ|ý|ỵ|ỷ|ỹ/g,"y").replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g,"u").replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ.+/g,"o").replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ.+/g, "e").replace(/ì|í|ị|ỉ|ĩ/g,"i");
			var name=""+list[i].name.toLowerCase().replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a").replace(/\ /g, '-').replace(/đ/g, "d").replace(/đ/g, "d").replace(/ỳ|ý|ỵ|ỷ|ỹ/g,"y").replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g,"u").replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ.+/g,"o").replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ.+/g, "e").replace(/ì|í|ị|ỉ|ĩ/g,"i");
			var s=$scope.searchText.toLowerCase().replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a").replace(/\ /g, '-').replace(/đ/g, "d").replace(/đ/g, "d").replace(/ỳ|ý|ỵ|ỷ|ỹ/g,"y").replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g,"u").replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ.+/g,"o").replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ.+/g, "e").replace(/ì|í|ị|ỉ|ĩ/g,"i");
			var lever_code=""+list[i].lever_code;
			if(((code.indexOf(s)> -1) || (name.indexOf(s)> -1) || (lever_code.indexOf(s)>-1)) && name != ''){
				listTemp.push(list[i]);
			}
		}
		$scope.listProduct=listTemp;
	}
	var dataPickList = {
	};
	$scope.options={
			data:dataPickList
	}
	$scope.resultList={
			
	};
	$scope.findProductInShift=function(lpi,id){
		for(var item in lpi){
			if(lpi[item].id==id){
				return true;
			}
		}
		return false;
	} 
	$scope.checkProductInObject=function(obj,idproduct){
		for(key in obj){
			if(obj[key].id==idproduct){
				return true;
			}
		}
		return false;
	}
	$scope.$watch('resultList', function(newVal, oldVal){
		let d_danh=localStorage.getItem("identify");
		if($scope.resultList.data===undefined || $scope.resultList.data == null || (Object.keys($scope.resultList.data).length === 0 && $scope.resultList.data.constructor === Object)){
			$scope.filterListProduct();
		}else{
			let cdates=document.getElementById("datetimepicker1").value;
			let str_datestartshift1=cdates+" 00:00:00"
			let datestartshift1=new Date(str_datestartshift1.substring(3,6) + str_datestartshift1.substring(0,2) + str_datestartshift1.substring(5,str_datestartshift1.length));
			let timedss1=datestartshift1.getTime();
			$scope.listPalletLixSumaryTab2=[];
			let shift=$scope.getShiftLix($scope.shiftLixSelectTab2);
			let listPllet=[];
			let strListPalletLix=localStorage.getItem('listPalletLix');
			if(strListPalletLix !== undefined && strListPalletLix !=null){
				listPllet=JSON.parse(strListPalletLix);
			}
			for(let index  in  listPllet){
				let str_dateinpallet1=listPllet[index].date_start_shift;
				let dateinpallet1=new Date(str_dateinpallet1.substring(3,6) + str_dateinpallet1.substring(0,2) + str_dateinpallet1.substring(5,str_dateinpallet1.length));
				let timedip1=dateinpallet1.getTime();
				let check=$scope.checkProductInObject($scope.resultList.data,listPllet[index].productLix.id);
				if(listPllet[index].identify==d_danh && listPllet[index].teamLix.id == $scope.teamLixSelectTab2 && $scope.shiftLixSelectTab2 == listPllet[index].shiftLix.id && timedss1==timedip1 && check){
					listPllet[index].select=true;
					$scope.listPalletLixSumaryTab2.push(listPllet[index]);
				}
			}
			$scope.listPalletLixSumaryTab2.sort(function (a, b) {
		        let aSize = a.productLix.name;
		        let bSize = b.productLix.name;
		        let aLow = a.num_order;
		        let bLow = b.num_order;
		        console.log(aLow + " | " + bLow);

		        if(aSize == bSize)
		        {
		            return (aLow < bLow) ? -1 : (aLow > bLow) ? 1 : 0;
		        }
		        else
		        {
		            return (aSize < bSize) ? -1 : 1;
		        }
		    });
			
			
		}
	}, true);
	$scope.updatePalletDelivery=function(arr,dl){
		var listTemp=[]
		var strListPalletLix=localStorage.getItem('listPalletLix');
		if(strListPalletLix !== undefined && strListPalletLix !=null){
			listTemp=JSON.parse(strListPalletLix);
		}
		for(i in listTemp){
			var idpl=listTemp[i].id;
			if(arr.indexOf(idpl)!=-1){
				listTemp[i].delivery=dl;
			}
		}
		localStorage.setItem("listPalletLix",JSON.stringify(listTemp));
		
	}
	$scope.changeIdentifyPallet=function(){
		
	}
	$scope.doYouWantFilterPalletOnline=function(){
		var messages="Đây là những pallet tạo lúc online, nên tạo phiếu giao nhận lúc có mạng internet hoặc trên các máy có kết nối internet," +
				" bạn vẫn muốn tạo không ? "
		var previousWindowKeyDown = window.onkeydown;
		swal({
	        title: "Thông báo",
	        text: messages,
	        type: "warning",
	        showCancelButton: true,
	        confirmButtonColor: "#DD6B55",
	        confirmButtonText: "Đồng ý",
	        cancelButtonText: "Không",
	        confirmButtonColor: '#3085d6',
	        cancelButtonColor: '#d33',

	    }, function (isConfirm) {
	        window.onkeydown = previousWindowKeyDown;
	        if (isConfirm) {
	        	 $scope.$apply($scope.createDelivery, 2000);
	        	return;
	        }
	    });
		 
	}
	$scope.optionCreateDelivery=function(){
		if(!$scope.boolOff){
			$scope.doYouWantFilterPalletOnline();
		}else{
			$scope.createDelivery();
		}
		$scope.kiemTraKetNoi();
		console.log("sssss")
	}
	
	$scope.createDelivery=function(){
		
		$scope.deliverySumary={};
		if($scope.listPalletLixSumaryTab2 !== undefined && $scope.listPalletLixSumaryTab2 != null && $scope.listPalletLixSumaryTab2.length > 0){
			let listNoSelect=[];
			for(let k in $scope.listPalletLixSumaryTab2){
				if($scope.listPalletLixSumaryTab2[k].select==true){
					listNoSelect.push($scope.listPalletLixSumaryTab2[k]);
				}
				$scope.listPalletLixSumaryTab2[k].select=undefined;
			}
			let listPalletCurr=[];
			if(listNoSelect.length >0){
				let strPalletCurr=localStorage.getItem("listPalletLix");
				if(strPalletCurr !== undefined && strPalletCurr != null && strPalletCurr != "" && strPalletCurr !="undefined"){
					listPalletCurr=JSON.parse(strPalletCurr);
					if(listPalletCurr !=null && listPalletCurr.length > 0){
						let len=listPalletCurr.length;
						for(let s in listNoSelect){
							while(len--){
								if(listPalletCurr[len].id == listNoSelect[s].id){
									listPalletCurr.splice(len,1);
									break;
								}
							}
							len=listPalletCurr.length;
						}
					}
				}
				Number.prototype.padLeft = function(base,chr){
					   let  len = (String(base || 10).length - String(this).length)+1;
					   return len > 0? new Array(len).join(chr || '0')+this : this;
				}
				let d = new Date,
		        dformat = [ d.getDate().padLeft(),
		        	       (d.getMonth()+1).padLeft(),
		                    d.getFullYear()].join('/')+
		                    ' ' +
		                  [ d.getHours().padLeft(),
		                    d.getMinutes().padLeft(),
		                    d.getSeconds().padLeft()].join(':');
				let deliveryObj={
						id:0,
						voucher:'-100',
						createdDate:dformat,
						delivery_date:listNoSelect[0].date_start_shift,
						shiftLix:listNoSelect[0].shiftLix,
						teamLix:listNoSelect[0].teamLix,
						vourcher_import:'',
						listDeliveryDetail:[]
					}
				let keyValue={};
				for(let i=0 ;i< listNoSelect.length ;i++){
					let key=listNoSelect[i].productLix.id;
					let value=keyValue[key];
					if(value===undefined || value == null || (Object.keys(value).length === 0 && value.constructor === Object)){
						let nu=Number(listNoSelect[i].num_pallet);
						if(isNaN(nu)){
							nu=0;
						}
						keyValue[key]={
							id:0,
							productLix:listNoSelect[i].productLix,
							createDate:dformat,
							number:nu,
							str_pallet:[]
						}
						keyValue[key].str_pallet.push(listNoSelect[i].num_order);
					}else{
						let bnum=Number(listNoSelect[i].num_pallet);
						if(isNaN(bnum)){
							bnum=0;
						}
						value.number=(bnum + value.number);
						value.str_pallet.push(listNoSelect[i].num_order);
					}
					// cap nhat lai list pallet
					
				}
				let identify=localStorage.getItem('identify');
				for(let it in keyValue){
					deliveryObj.listDeliveryDetail.push(keyValue[it]);
					keyValue[it].str_pallet.sort((a, b) => a - b);
					let arrStrPall=keyValue[it].str_pallet;
					keyValue[it].str_pallet=[]
					for(let t in arrStrPall){
						keyValue[it].str_pallet.push(identify+"_"+arrStrPall[t]);
					}
					keyValue[it].str_pallet=keyValue[it].str_pallet.toString();
				}
				// pallet tao phieu giao nhan
				let objPalletDelivery={
						listPallet:listNoSelect
				}
				
				
				let strListDelivery=localStorage.getItem("listDelivery")
				let listDelivery=[];
				// delevery obj co dang nhu sau.
				/*
				 * obj={ delivery:deliveryObj, deliveryInfo:objPalletDelivery }
				 */
				let idTemp=1;
				if(strListDelivery !==undefined && strListDelivery !=null && strListDelivery != "" && strListDelivery !="undefined"){
					listDelivery=JSON.parse(strListDelivery);
					// xac dinh id (-) nho nhat.
					for(let io in listDelivery){
						if(listDelivery[io].delivery.id < idTemp){
							idTemp=listDelivery[io].delivery.id ;
						}
					}
					
				}
				deliveryObj.id=idTemp-1;
				let objSummary={
						delivery:deliveryObj,
						deliveryInfo:objPalletDelivery
				}
				listDelivery.push(objSummary);
				localStorage.setItem("listDelivery",JSON.stringify(listDelivery));
				$scope.deliverySumary=deliveryObj;
				for(let i in $scope.deliverySumary.listDeliveryDetail){
					let tong=$scope.deliverySumary.listDeliveryDetail[i].number*$scope.deliverySumary.listDeliveryDetail[i].productLix.kgbox*$scope.deliverySumary.listDeliveryDetail[i].productLix.slsp;
					$scope.deliverySumary.listDeliveryDetail[i].kl=MyService.change_currency(tong);
				}
				
                $scope.printDelivery();
				
				
				// luu lai danh sach sau khi thanh công
				if(strPalletCurr !== undefined && strPalletCurr != null && strPalletCurr != "" && strPalletCurr !="undefined"){
						localStorage.setItem("listPalletLix",JSON.stringify(listPalletCurr));
						
				}
				
				$scope.listPalletLixSumaryTab2=[];
		   }
		}
		$scope.filterListProduct();
		$scope.getListButton();
	}
	$scope.filterListProductOnline=function(){
		$scope.boolOff=false;
		$scope.listPalletLixSumaryTab2=[];
		let cdates=document.getElementById("datetimepicker1").value;
		if($scope.teamLixSelectTab2 !=0 && $scope.shiftLixSelectTab2 !=0 && cdates !=""){
				let data={};
				$scope.resultList={data:{}};
				let shift=$scope.getShiftLix($scope.shiftLixSelectTab2);
				// load danh sach product
				let listPllet=[];
				let strListPalletLix=localStorage.getItem('listPalletLix');
				if(strListPalletLix !== undefined && strListPalletLix !=null && strListPalletLix !="" && strListPalletLix !="undefined"){
					listPllet=JSON.parse(strListPalletLix);
				}
				let listRealSelect=[];
				let str_datestartshift=cdates+" 00:00:00"
				let datestartshift=new Date(str_datestartshift.substring(3,6) + str_datestartshift.substring(0,2) + str_datestartshift.substring(5,str_datestartshift.length));
				let timedss=datestartshift.getTime();
				for(let index  in  listPllet){
					let str_dateinpallet=listPllet[index].date_start_shift;
					let dateinpallet=new Date(str_dateinpallet.substring(3,6) + str_dateinpallet.substring(0,2) + str_dateinpallet.substring(5,str_dateinpallet.length));
					let timedip=dateinpallet.getTime();
					if(listPllet[index].num_orderoff == 0 && listPllet[index].teamLix.id == $scope.teamLixSelectTab2 && $scope.shiftLixSelectTab2 == listPllet[index].shiftLix.id && timedip==timedss){
						listRealSelect.push(listPllet[index]);
					}
				}
				// chuan hoa lai list real no co con nam trong phieu giao nhan
				// chua.
				let strLoalDelivery = localStorage.getItem("listPalletDelivery");
				if(strLoalDelivery != undefined && strLoalDelivery != null && strLoalDelivery !="" && strLoalDelivery != "undefined"){
					let listLocalDelivery=JSON.parse(strLoalDelivery);
					let plength=listRealSelect.length;
					let plength2=listLocalDelivery.length;
					while(plength--){
						while(plength2--){
							if(listRealSelect[plength].id==listLocalDelivery[plength2].id){
								listRealSelect.splice(plength,1);
								break;
							}
						}
						plength2=listLocalDelivery.length;
					}
				}
				for(let p2 in listRealSelect){
					$scope.listPalletLixSumaryTab2.push(listRealSelect[p2]);
					let j=p2+1;
					let key = (i<9 ? '0'+j :j+'');// lay key.... '08', '09'
													// '10','11'...
					let check=$scope.findProductInShift(data,listRealSelect[p2].productLix.id);
					if(!check){
						data[key]={
							id:listRealSelect[p2].productLix.id,
							text:listRealSelect[p2].productLix.name
						}
					}
				}
				$scope.options={
						data:data
				}
		
				
			}
		    if($scope.listPalletLixSumaryTab2 !=undefined && $scope.listPalletLixSumaryTab2 != null && $scope.listPalletLixSumaryTab2.length >0){
		    	for(let k in $scope.listPalletLixSumaryTab2){
		    		$scope.listPalletLixSumaryTab2[k].select=true;
		    	}
				
			}
		    $scope.listPalletLixSumaryTab2.sort(function(a, b){
		    	let a1=a.num_order;
		    	let b1=b.num_order;
		        return a1 - b1;
		    });
	}
	$scope.filterListProduct=function(){
		$scope.boolOff=true;
			$scope.listPalletLixSumaryTab2=[];
			let d_danh=localStorage.getItem("identify");
			if(d_danh !==undefined && d_danh !=null && d_danh !="" && d_danh !="undefined"){
				let cdates=document.getElementById("datetimepicker1").value;
				if($scope.teamLixSelectTab2 !=0 && $scope.shiftLixSelectTab2 !=0 && cdates !=""){
						let data={};
						$scope.resultList={data:{}};
						let shift=$scope.getShiftLix($scope.shiftLixSelectTab2);
						// load danh sach product
						let listPllet=[];
						let strListPalletLix=localStorage.getItem('listPalletLix');
						if(strListPalletLix !== undefined && strListPalletLix !=null && strListPalletLix !="" && strListPalletLix !="undefined"){
							listPllet=JSON.parse(strListPalletLix);
						}
						let listRealSelect=[];
						let str_datestartshift=cdates+" 00:00:00"
						let datestartshift=new Date(str_datestartshift.substring(3,6) + str_datestartshift.substring(0,2) + str_datestartshift.substring(5,str_datestartshift.length));
						let timedss=datestartshift.getTime();
						for(let index  in  listPllet){
							let str_dateinpallet=listPllet[index].date_start_shift;
							let dateinpallet=new Date(str_dateinpallet.substring(3,6) + str_dateinpallet.substring(0,2) + str_dateinpallet.substring(5,str_dateinpallet.length));
							let timedip=dateinpallet.getTime();
							if(listPllet[index].identify==d_danh && listPllet[index].teamLix.id == $scope.teamLixSelectTab2 && $scope.shiftLixSelectTab2 == listPllet[index].shiftLix.id && timedip==timedss){
								listRealSelect.push(listPllet[index]);
							}
						}
						// chuan hoa lai list real no co con nam trong phieu
						// giao nhan chua.
						let strLoalDelivery = localStorage.getItem("listPalletDelivery");
						if(strLoalDelivery != undefined && strLoalDelivery != null && strLoalDelivery !="" && strLoalDelivery != "undefined"){
							let listLocalDelivery=JSON.parse(strLoalDelivery);
							let plength=listRealSelect.length;
							let plength2=listLocalDelivery.length;
							while(plength--){
								while(plength2--){
									if(listRealSelect[plength].id==listLocalDelivery[plength2].id){
										listRealSelect.splice(plength,1);
										break;
									}
								}
								plength2=listLocalDelivery.length;
							}
						}
						for(let p2 in listRealSelect){
							$scope.listPalletLixSumaryTab2.push(listRealSelect[p2]);
							let j=p2+1;
							let key = (i<9 ? '0'+j :j+'');// lay key.... '08',
															// '09' '10','11'...
							let check=$scope.findProductInShift(data,listRealSelect[p2].productLix.id);
							if(!check){
								data[key]={
									id:listRealSelect[p2].productLix.id,
									text:listRealSelect[p2].productLix.name
								}
							}
						}
						$scope.options={
								data:data
						}
				
						
					}
				    if($scope.listPalletLixSumaryTab2 !=undefined && $scope.listPalletLixSumaryTab2 != null && $scope.listPalletLixSumaryTab2.length >0){
				    	for(let k in $scope.listPalletLixSumaryTab2){
				    		$scope.listPalletLixSumaryTab2[k].select=true;
				    	}
						
					}

				    $scope.listPalletLixSumaryTab2.sort(function (a, b) {
				        let aSize = a.productLix.name;
				        let bSize = b.productLix.name;
				        let aLow = a.num_order;
				        let bLow = b.num_order;
				        console.log(aLow + " | " + bLow);

				        if(aSize == bSize)
				        {
				            return (aLow < bLow) ? -1 : (aLow > bLow) ? 1 : 0;
				        }
				        else
				        {
				            return (aSize < bSize) ? -1 : 1;
				        }
				    });
			}else{
//				 swaldesigntimer('Thất bại', 'Định danh2 máy chưa cài đặt!','error',2000);
			}
	}
	$scope.locSanPhamOffline=function(){
		$scope.filterListProduct();
		$scope.kiemTraKetNoi();
	}
	$scope.closeDialogAuthen = function(){
		q$("#myModal1").modal("hide");
	}
	$scope.closeDialogChinhSua=function(){
		q$("#myModal2").modal("hide");
	}
	$scope.closeDialogDataOffline=function(){
		q$("#myModal123").modal("hide");
	}
	$scope.closeDialogBarcode=function(){
		q$("#myModal").modal("hide");
	}
	$scope.showDialogData = function(){
		q$("#myModal123").modal({
			backdrop: 'static'
	 });
	 q$('#myModal123 .modal-dialog').draggable({
		    handle: ".modal-header"
      });
	 $scope.duLieuOffline();
	}
	$scope.dataPalletOffline=[];
	$scope.checkDataPalletOffline=function(pallet){
			if($scope.dataPalletOffline.length >0){
				for(let index in $scope.dataPalletOffline){
					 let date=$scope.dataPalletOffline[index].date_start_shift;
					 let product_id=$scope.dataPalletOffline[index].product_id;
					 let shift_id=$scope.dataPalletOffline[index].shift_id;
					 let team_id=$scope.dataPalletOffline[index].team_id;
					 if(pallet.date_start_shift==date && pallet.productLix.id==product_id && pallet.shiftLix.id==shift_id && pallet.teamLix.id==team_id){
						 $scope.dataPalletOffline[index].summary_pallet +=1;
						 $scope.dataPalletOffline[index].summary_box+=pallet.num_pallet;
						 $scope.dataPalletOffline[index].summary_kg+=(pallet.productLix.kgbox * pallet.productLix.slsp *pallet.num_pallet);
						 return;
					 }
				}
				let obj={
						date_start_shift:pallet.date_start_shift,
						product_id:pallet.productLix.id,
						product_code:pallet.productLix.product_code,
						product_name:pallet.productLix.name,
						summary_pallet:1,
						summary_box:pallet.num_pallet,
						summary_kg:(pallet.productLix.kgbox * pallet.productLix.slsp *pallet.num_pallet),
						shift_id:pallet.shiftLix.id,
						shift_name:pallet.shiftLix.name,
						team_id:pallet.teamLix.id,
						team_name:pallet.teamLix.name
				}
			   $scope.dataPalletOffline.push(obj);
			}else{
				
					let obj={
							id:pallet.id,
							date_start_shift:pallet.date_start_shift,
							product_id:pallet.productLix.id,
							product_code:pallet.productLix.product_code,
							product_name:pallet.productLix.name,
							summary_pallet:1,
							summary_box:pallet.num_pallet,
							summary_kg:(pallet.productLix.kgbox * pallet.productLix.slsp *pallet.num_pallet),
							shift_id:pallet.shiftLix.id,
							shift_name:pallet.shiftLix.name,
							team_id:pallet.teamLix.id,
							team_name:pallet.teamLix.name
					}
				   $scope.dataPalletOffline.push(obj);
			}
	}
	$scope.dataDeliveryOffline=[];
	$scope.duLieuOffline=function(){
		$scope.dataPalletOffline=[];
		$scope.dataDeliveryOffline=[];
		let strlistPalletIn=localStorage.getItem("listPalletLix");
		if(strlistPalletIn != undefined && strlistPalletIn != null && strlistPalletIn !="" && strlistPalletIn != "undefined"){
			let listPalletIn=JSON.parse(strlistPalletIn);
			for(let index in listPalletIn){
				$scope.checkDataPalletOffline(listPalletIn[index])
			}
			
		}
		let strListDelivery=localStorage.getItem("listDelivery");
		if(strListDelivery != undefined && strListDelivery != null && strListDelivery !="" && strListDelivery != "undefined"){
			let listDelivery=JSON.parse(strListDelivery);
			let listPalletInDelivery=[];
			for(let index in listDelivery){
				let list=listDelivery[index].deliveryInfo.listPallet;
				for(let i in list){
					listPalletInDelivery.push(list[i]);
				}
				let objdeli={
				    id:listDelivery[index].delivery.id,
				    createdDate:listDelivery[index].delivery.createdDate,
				    delivery_date:listDelivery[index].delivery.delivery_date,
				    shift_name:listDelivery[index].delivery.shiftLix.name,
				    team_name:listDelivery[index].delivery.teamLix.name
				}
				$scope.dataDeliveryOffline.push(objdeli);
			}
			for(let index in listPalletInDelivery){
				$scope.checkDataPalletOffline(listPalletInDelivery[index]);
			}
			
		}
		
	}
	$scope.deleteDeliveryData=function(id){
		let strListDelivery=localStorage.getItem("listDelivery");
		if(strListDelivery != undefined && strListDelivery != null && strListDelivery !="" && strListDelivery != "undefined"){
			let listDelivery=JSON.parse(strListDelivery);
			for( var i = 0; i < listDelivery.length; i++){ 
				   if ( listDelivery[i].delivery.id == id) {
					   let listPalletInDelivery=listDelivery[i].deliveryInfo.listPallet;
					   let listPallet=[];
					   listDelivery.splice(i, 1); 
					   localStorage.setItem("listDelivery",JSON.stringify(listDelivery));
					   let strListPallet=localStorage.getItem("listPalletLix");
					   if(strListPallet != undefined && strListPallet != null && strListPallet !="" && strListPallet != "undefined"){
						   listPallet=JSON.parse(strListPallet);
						  
					   }
					   for(let index in listPalletInDelivery){
						   listPallet.push(listPalletInDelivery[index]);
					   }
					   localStorage.setItem("listPalletLix",JSON.stringify(listPallet));
					   break;
				   }
			}
			$scope.duLieuOffline();
		}
	}
	$scope.dataViewDelivery=[];
	$scope.viewDetailDelivery=function(id){
		$scope.dataViewDelivery=[];
		let strListDelivery=localStorage.getItem("listDelivery");
		if(strListDelivery != undefined && strListDelivery != null && strListDelivery !="" && strListDelivery != "undefined"){
			let listDelivery=JSON.parse(strListDelivery);
			for(let index in listDelivery){
				if(listDelivery[index].delivery.id == id){
					  let listDetailDelivery=listDelivery[index].delivery.listDeliveryDetail;
					  for(let p in listDetailDelivery){
						  $scope.dataViewDelivery.push(listDetailDelivery[p]);
					  }
					  
				}
			}
		}
		q$("#myModal5").modal({
			backdrop: 'static'
	   });
	    q$('#myModal5 .modal-dialog').draggable({
			    handle: ".modal-header"
	    });
	}
	$scope.closeDialogViewDetailDelivery=function(){
		q$("#myModal5").modal("hide");
	}
	$scope.closeDialogOfflineAngu=function(){
		q$("#myModalOffline").modal("hide");
	}
	$scope.actionDongBo=function(){
		MyService.checkServerOffline("palletlix","ping","",checkResComplete,checkResError);
		function checkResComplete(response){
			document.getElementById("nupdongboid").disabled = true;
			$scope.asynDataSpecial();
		}
		function checkResError(err){
			swaldesigntimer('Thất bại', 'Chưa có kết nối internet không thể đồng bộ được!','error',2000);
		}
	}
	$scope.kiemTraKetNoi=function(){
		MyService.checkServerOffline("palletlix","ping","",checkResComplete,checkResError);
		function checkResComplete(response){
			alert('Đã có kết nối internet bạn hãy bấm nút đồng bộ dữ liệu và đồng bộ tất cả dữ liệu về.');
		}
		function checkResError(err){
		}
	}
	$scope.complete=function(string){
		if(string===undefined || string==null || string=="" ||string =="undefined" ){
			$scope.filterProductNew=null;
			$scope.productCompleteNew=null;
			$scope.productBind=null;
		}else if(string.length>=2){
			let output=[];
			let str=string.toLowerCase();
			let strListProduct=localStorage.getItem("listProduct");
			if(strListProduct!==undefined && strListProduct !=null && strListProduct !="" && strListProduct != "undefined" ){
				let listP=JSON.parse(strListProduct);
				for(let i in listP){
					let code=listP[i].product_code.toLowerCase();
					let name =listP[i].name.toLowerCase();
					if((code.indexOf(str)>-1) || (name.indexOf(str)>-1)){
				    	output.push(listP[i]);
				    }
				}
			}
			$scope.filterProductNew=output;
		}else{
			$scope.filterProductNew=null;
		}
	}
	$scope.fillTextbox=function(obj){
		$scope.productBind=obj.name;
		$scope.productCompleteNew=obj;
		$scope.filterProductNew=null;
	}
	$scope.deleteItemBarcode=function(bar){
		let strListBarcode=localStorage.getItem("listBarcode");
		if(strListBarcode !==undefined && strListBarcode !=null && strListBarcode !="" && strListBarcode != "undefined"){
			let listBar=JSON.parse(strListBarcode);
			for(let i=0;i<listBar.length;i++){
				if(listBar[i].barcode==bar){
					listBar.splice(i,1);
					localStorage.setItem("listBarcode",JSON.stringify(listBar));
					$scope.getListButton();
					break;
				}
			}
		}
	}
	$scope.deleteAllPalletOnline=function(){
		
		let messages="Bạn có muốn xóa tất cả các pallet online để tập hợp dữ liệu cho đúng không?"
		let previousWindowKeyDown = window.onkeydown;
		swal({
		    title: "Thông báo",
		    text: messages,
		    type: "warning",
		    showCancelButton: true,
		    confirmButtonColor: "#DD6B55",
		    confirmButtonText: "Đồng ý",
		    cancelButtonText: "Không",
		    confirmButtonColor: '#3085d6',
		    cancelButtonColor: '#d33',
		
		}, function (isConfirm) {
		    window.onkeydown = previousWindowKeyDown;
		    if (isConfirm) {
		    	let strListPallet=localStorage.getItem("listPalletLix");
				if(strListPallet !==undefined && strListPallet !=null && strListPallet !="" && strListPallet !="undefined"){
					let list=JSON.parse(strListPallet);
					let len=list.length;
					while(len--){
						if(list[len].id>0){
							list.splice(len,1);
						}
					}
					localStorage.setItem("listPalletLix",JSON.stringify(list));
					$scope.filterListProduct();
					$scope.getListButton();
				}
		    }
		});
	}
	$scope.rePrintDelivery=function(id){
		let strListDelivery=localStorage.getItem("listDelivery");
		if(strListDelivery !== undefined && strListDelivery !=null && strListDelivery !="" && strListDelivery !="undefined"){
			let list=JSON.parse(strListDelivery);
			let obj=null;
			for(let i in list){
				if(list[i].delivery.id==id){
					obj=list[i].delivery;
					break;
				}
			}
			if(obj!=null){
				$scope.deliverySumary=obj;
				$scope.printDelivery();
				
			}
		}
	}
	$scope.dateStartShift=$scope.getFormattedDateShort(new Date());
});
angular.module('aw-picklist', [])
.directive('pickList', function(){
	

var defaults = {};

return {
	scope: {
    	options: '=',
        result: '=',
        listdata: '=',
    },
    templateUrl: 'component/pickListTmpl.html',
	link: function(scope, element, attrs) {
    	var opts = angular.extend({}, defaults, scope.options);
        // var $el = $(element).multiselect(opts); // jquery plugin not required
        function copy(pickListSelect, source, target) {
        	// add data to new list
            var i, id;
            // copy to new lsit & remove old element
            for(i=0; i< pickListSelect.length; i++) {
            	id=pickListSelect[i].id;
            	target.data[id] = target.data[id] || {};
                angular.extend(target.data[id], pickListSelect[i]);
			
                delete source.data[pickListSelect[i].id];
            }
			
            pickListSelect = [];
        }
        
        angular.extend(scope, {
            pickListSelect: [],
            pickListResultSelect: [],
            result: {
                data: {}
            },
        	add: function() {
            	var id;
            	console.log("aaa")
            	// copy(selection, source, target)
                copy(scope.pickListSelect, scope.options, scope.result);
            	var data=scope.options['data'];
            	for(var i in data){
            		console.log(data[i]);
            		for(var j in scope.pickListSelect){
            			if(data[i].id==scope.pickListSelect[j].id){
            				delete data[i];
            				break;
            			}
            		}
            	}
            	
                var resultPickRight=scope.result['data']
                for(key in resultPickRight){
                	console.log("key :"+key);
                }
                
// var strListPalletLix1=localStorage.getItem('listPalletLix');
// if(strListPalletLix1 !== undefined && strListPalletLix1 !=null){
// var dsPallet=JSON.parse(strListPalletLix1);
// for(index in dsPallet){
// if(dsPallet[index].delivery ==undefined || dsPallet[index].delivery == null){
// for(index1 in resultPickRight){
// if(dsPallet[index].productLix.id==index1){
// scope.listdata.push(dsPallet[index]);
// break;
// }
// }
// }
// }
// }
// console.log(scope.listdata);
// delete scope.options[];
            },
            addAll: function() {
            	angular.merge(scope.result.data, scope.options.data);
                scope.options.data = {};
            },
            remove: function() {
            	console.log(scope.result)
            	copy(scope.pickListResultSelect, scope.result, scope.options);
            },
            removeAll: function() {
            	angular.merge(scope.options.data, scope.result.data);
                scope.result.data = {};
            }
        });
    }
};
});