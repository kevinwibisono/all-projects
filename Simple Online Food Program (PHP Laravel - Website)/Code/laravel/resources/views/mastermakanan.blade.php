<!DOCTYPE html>
<html style="min-height:100%" lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
    <link rel="stylesheet" type="text/css" href="{{ asset('/css/app.css') }}">
    <script src="{{asset('/js/app.js')}}"></script>

    <script src="DataTables-1.10.20/js/jquery.dataTables.js"></script>
    <script src="DataTables-1.10.20/js/dataTables.bootstrap4.min.js"></script>
    <script type="text/javascript" src="DataTables-1.10.20/js/jquery.dataTables.js"></script>
	<style>
		.popup{
		  display: none;
		  position: fixed;
		  z-index: 1;
		  padding-top: 30px;
		  left: 0;
		  top: 0;
		  width : 100%;
		  height: 100%;
		  text-align: center;
		}

		.isipopup {
		  background-color: white;
		  margin: auto;
		  padding: 20px;
		  border: 1px solid black;
		  width: 80%;
		}

        .trans{
			text-align: center;
			border: 2px solid black;
			padding: 0px;
			border-radius: 10px;
			width: 650px;
			margin-bottom: 10px;
			padding: 10px;
			margin: auto;
		}

        .dataTables_wrapper .dataTables_paginate .paginate_button {
            color: black !important;
            border: 1px solid #2980B9!important;
            background-color: #2980B9!important;
            background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #2980B9), color-stop(100%, #2980B9))!important;
            background: -webkit-linear-gradient(top, #2980B9 0%, #2980B9 100%)!important;
            background: -moz-linear-gradient(top, #2980B9 0%, #2980B9 100%)!important;
            background: -ms-linear-gradient(top, #2980B9 0%, #2980B9 100%)!important;
            background: -o-linear-gradient(top, #2980B9 0%, #2980B9 100%)!important;
            background: linear-gradient(to bottom, #2980B9 0%, #2980B9 100%)!important;
        }

        .dataTables_wrapper .dataTables_paginate .paginate_button:hover {
            color: white !important;
            border: 1px solid #2980B9!important;
            background-color: #2980B9!important;
            background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #2980B9), color-stop(100%, #2980B9))!important;
            background: -webkit-linear-gradient(top, #2980B9 0%, #2980B9 100%)!important;
            background: -moz-linear-gradient(top, #2980B9 0%, #2980B9 100%)!important;
            background: -ms-linear-gradient(top, #2980B9 0%, #2980B9 100%)!important;
            background: -o-linear-gradient(top, #2980B9 0%, #2980B9 100%)!important;
            background: linear-gradient(to bottom, #2980B9 0%, #2980B9 100%)!important;
        }
	</style>
</head>
<body style = "background-image:url({{asset('/images/bg2.jpg')}});background-position:center;background-repeat:no-repeat;background-size:cover;height:100%">
	<script>
		function displaymodal(i){
            console.log(i);
            var id = document.getElementById('HiddenMenu['.concat(i, ']')).value;
            var nama = document.getElementById('HiddenNamaMenu['.concat(i, ']')).value;
            var harga = document.getElementById('HiddenHargaMenu['.concat(i, ']')).value;
            document.getElementById('newnama').value = nama;
            document.getElementById('newharga').value = harga;
            document.getElementById('hiddenid').value = id;
		}

        $(document).ready(function () {
            $('#data_makanan').DataTable({
                "pagingType": "simple"
            });
            $('.dataTables_length').addClass('bs-select');
        });
    </script>
    @include('inc.navbar_home_merchant')
    <main role="main" class="container">
        <div style="margin:0 auto;background-color: grey" class="jumbotron col-md-10">
            <div class="text-center mb-6">
                <div class="form-label-group">
                    <div id="header">
                        <div>
                            <h1>Welcome to food management, {{$current_user->Nama}}</h1>
                        </div>
                        <div style="">
                            <h3>You can edit your food here</h3>
                        </div>
                    </div>
                </div>

                <div class ="form-label-group">
                    <div id='content' style="clear:left; opacity: 0.75; margin: auto; padding: 30px;">
                        <form style="display:table;margin:auto" action="{{url('/handle_insert_food')}}" method='post' enctype="multipart/form-data">
                            {{csrf_field()}}
                            <label style="display:table-row"> Nama Makanan: </label> <input type="text" name="makanan"><br>
                            @if(isset($error))
                                @if($error->has('makanan'))
                                <b style="color: red;">{{$error->first('makanan')}}</b><br>
                                @endif
                            @endif
                            <br>
                            <label style="display:table-row"> Harga: </label> <input type="text" name="harga"><br>
                            @if(isset($error))
                                @if($error->has('harga'))
                                <b style="color: red;">{{$error->first('harga')}}</b><br>
                                @endif
                            @endif
                            <br>
                            <label style="display:table-row"> Menu Image: </label> <input type="file" name="menuimg">
                            <br>
                            @if(isset($error))
                                @if($error->has('menuimg'))
                                    <b style='color:red;'>{{$error->first('menuimg')}}</b><br>
                                @endif
                            @endif
                            <br>
                            <input class="btn btn-dark btn-block" type="submit" value='Insert'><br><br>
                        </form>
                        <table id="data_makanan" class="table table-hover table-dark"  style='margin: 0 auto'>
                            <thead>
                                <th class="th-sm">No</th>
                                <th class="th-sm">Image</th>
                                <th class="th-sm">Nama Menu</th>
                                <th class="th-sm">Harga</th>
                                <th class="th-sm">Action</th>
                            </thead>
                            <?php $nourut = 0;?>
                            @foreach($menu as $m)
                            <?php $nourut+=1;?>
                            <tr>
                                <td><?php echo $nourut;?></td>
                                <td style="object-fit:cover;display:block;width:75%;height:auto"><img src="{{asset('/images/menu/'.$m->Id_menu.'.jpg')}}" alt="No Images" style="width: 100%; height: 20%;"></td>
                                <td>{{$m->Nama}}</td>
                                <td>{{$m->Harga}}</td>
                                <td>
                                    <input type="hidden" id="HiddenMenu[<?=$nourut?>]" value="{{$m->Id_menu}}">
                                    <input type="hidden" id="HiddenNamaMenu[<?=$nourut?>]" value="{{$m->Nama}}">
                                    <input type="hidden" id="HiddenHargaMenu[<?=$nourut?>]" value="{{$m->Harga}}">
                                    <button type='button' name='EDIT' onclick='displaymodal(<?=$nourut?>)' data-toggle='modal' data-target='#editModal'>Edit</button>
                                    <form action="{{url('/delete_food')}}" method="post">
                                    {{csrf_field()}}
                                        <input type="hidden" name="HiddenMenu" value="{{$m->Id_menu}}">
                                        <input type="submit" value="Delete">
                                    </form>
                                </td>
                            </tr>
                            @endforeach
                        </table>
                        <br><br>
                        <h3>Terima Transaksi</h3>
                        @foreach($hpesan as $h)
                            <div class='trans'>
                            <h3>Pembeli&nbsp:&nbsp{{$h->Pembeli->Nama}}</h3><br>
                            Status&nbsp:&nbsp<b>{{$h->Status}}</b><br><br>
                            Tanggal&nbsp:&nbsp<b>{{$h->Tanggal}}</b><br><br>
                            Menuju&nbsp:&nbsp<b>{{$h->Alamat_pengiriman}}</b><br><br>
                            <table style="margin: 0 auto;">
                            <hr>
                            <h3>Detail</h3>
                            @foreach ($dpesan as $d)
                                @if($d->Id_pesanan == $h->Id_pesanan)
                                <tr>
                                    <td><?php echo $d->Nama_menu;?></td>
                                    <td><?php echo 'x '.$d->Jumlah_menu;?></td>
                                    <td><?php echo 'Rp '.$d->Jumlah_menu*$d->Harga_menu;?></td>
                                </tr>
                                @endif
                            @endforeach
                            <tr>
                                <td>Total</td>
                                <td></td>
                                <td>Rp&nbsp{{$h->Total}}</td>
                            </tr>
                            </table>
                            <br>
                            <form action="{{url('/accept_order')}}" method="post">
                                {{ csrf_field() }}
                                <input type="hidden" name="HiddenPesan" value="{{$h->Id_pesanan}}">
                                <input type="submit" value="Accept">
                            </form>
                            </div>
                        @endforeach
                    </div>
                </div>
            </div>
        </div>
    </main>

    <div class="modal" tabindex="-1" role="dialog" id="editModal">
        <div class="modal-dialog" role="document">
                <div class = "modal-content">
                    <div class ="modal-header">
                        <h3 class="modal-title">Edit Menu</h3>
                    </div>
                    <form action="{{url('/edit_food')}}" method='post'>
                        {{csrf_field()}}
                    <div class="modal-body">
                        <div class="form-group">
                            <input id="newnama" type="text" name="nama_menu" class="form-control" placeholder="Nama Baru">
                        </div>
                        <div class="form-group">
                            <input id="newharga" type="text" name="new_harga" class="form-control" placeholder="Harga Baru">
                        </div>
                        <div class="form-group">
                            <input id="hiddenid" type="hidden" name="hidden_id" class="form-control" placeholder="Harga Baru">
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button id="submit_edit" name='EDIT' type="submit" class="btn btn-success">Save</button>
                        <button type="button" class="close" data-dismiss="modal">Cancel</button>
                    </div>
                    </form>
                </div>
        </div>
    </div>

</body>
</html>
