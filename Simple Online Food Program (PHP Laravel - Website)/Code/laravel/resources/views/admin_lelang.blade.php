<!DOCTYPE html>
<html style="min-height:100%" lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" type="text/css" href="{{ asset('/css/app.css') }}">
    <title>Document</title>

    
    <script src="{{asset('/js/app.js')}}"></script>
    <script src="DataTables-1.10.20/js/jquery.dataTables.js"></script>
    <script src="DataTables-1.10.20/js/dataTables.bootstrap4.min.js"></script>
    <script type="text/javascript" src="DataTables-1.10.20/js/jquery.dataTables.js"></script>
    <style>
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

        .page-item.disabled .page-link{
            background-color: #D6DBE0;
        }
    </style>
</head>
<body style = "background-image:url({{asset('/images/bg2.jpg')}});background-position:center;background-repeat:no-repeat;background-size:cover;min-height:100%">
<script>
        $(document).ready(function () {
            $('#data_lelang').DataTable({
                "pagingType": "simple"
            });
            $('.dataTables_length').addClass('bs-select');
        });
    </script>
    @include('inc.navbar_admin')
    <hr>
    <main role="main" class="container">
		<div style="float:none;margin:0 auto;background-color:grey; opacity:0.9" class="jumbotron col-md-6">
			<div class="text-center mb-4">
            <h1>Tambah Lelang</h1>

				<div class="form-label-group">
                    <form style="display:table;margin:auto" action="{{url('/insert_lelang')}}" method="post" enctype="multipart/form-data">
                        {{csrf_field()}}
                        <label style="display:table-row"> Judul Lelang: </label> <input type='text' name='judul'><br>
                        @if(isset($error))
                            @if($error->has('judul'))
                                <b style='color:red;'>{{$error->first('judul')}}</b><br>
                            @endif
                        @endif
                        <br>
                        <label style="display:table-row"> Nama Barang: </label> <input type='text' name='nama'><br>
                        @if(isset($error))
                            @if($error->has('nama'))
                                <b style='color:red;'>{{$error->first('nama')}}</b><br>
                            @endif
                        @endif
                        <br>
                        <label style="display:table-row"> Bid User:Rp. </label> <input type='number' name='bid' min='0' step='1000'><br>
                        @if(isset($error))
                            @if($error->has('bid'))
                                <b style='color:red;'>{{$error->first('bid')}}</b><br>
                            @endif
                        @endif
                        <br>
                        <label style="display:table-row"> Waktu Akhir Lelang: </label> <input type='datetime-local' name='time_start'><br>
                        @if(isset($error))
                            @if($error->has('time_start'))
                                <b style='color:red;'>{{$error->first('time_start')}}</b><br>
                            @endif
                        @endif
                        <hr>
                        <h4>Display Image</h4>
                        <input type="file" name="myfile" id=""><br>
                        @if(isset($error))
                            @if($error->has('myfile'))
                                <b style='color:red;'>{{$error->first('myfile')}}</b><br>
                            @endif
                        @endif

                        <hr>
                        <br><br>
                        <input type='submit' class="btn btn-dark btn-block" value="ADD">
                        <h1><?php if(isset($msg)){
                            echo $msg;
                        } ?></h1>
                    </form>
				</div>

				<div class ="form-label-group">

				</div>

			</div>
		</div>
	</main>
    <br>
    <br>
    <main role="main" class="container">
      <table id="data_lelang" class="table table-hover table-dark">
        <thead>
        <tr>
            <th class="th-sm">Gambar</th>
            <th class="th-sm">ID Lelang</th>
            <th class="th-sm">Judul Lelang</th>
            <th class="th-sm">Barang</th>
            <th class="th-sm">Waktu Lelang</th>
            <th class="th-sm">Max Bid</th>
            <th class="th-sm">Top Bidder</th>
        </tr>
        </thead>
        <tbody>
        <?php
            foreach ($lelang as $row) {
                echo "<tr>";
        ?>
                <td style="object-fit:cover;display:block;width:75%;height:auto"><img src="{{asset('/images/lelang/'.$row->Id_lelang.'.jpg')}}" alt="No Images" style="width: 100%; height: 20%;"></td>
        <?php
                echo "<td>".$row->Id_lelang."</td>";
                echo "<td>".$row->Nama_lelang."</td>";
                echo "<td>".$row->Barang."</td>";
                echo "<td>".$row->Time_end."</td>";
                echo "<td> Rp. ".$row->Current_bid.",00,-</td>";
                echo "<td>".$row->Top_bidder."</td>";
                echo "</tr>";
            }
        ?>
        </tbody>
      </table>
    </main>
</body>
</html>

