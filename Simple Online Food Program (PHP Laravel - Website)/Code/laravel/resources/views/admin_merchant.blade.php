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
        
    </style>
</head>
<body style = "background-image:url({{asset('/images/bg2.jpg')}});background-position:center;background-repeat:no-repeat;background-size:cover;min-height:100%">
    <script>
        $(document).ready(function () {
            $('#data_merchant').DataTable({
                "pagingType": "simple"
            });
            $('.dataTables_length').addClass('bs-select');
        });
    </script>
    @include('inc.navbar_admin')

    <main role="main" class="container">
		<div style="float:none;margin:0 auto;background-color:grey; opacity:0.9" class="jumbotron col-md-6">
			<div class="text-center mb-4">
				<div class="form-label-group">
                <h1>Data Merchant</h1>
                    <hr>
                    <table id="data_merchant" class="table table-hover table-dark" >
                        <tr>
                            <th class="th-sm">Nama</th>
                            <th class="th-sm">Alamat</th>
                            <th class="th-sm">Block</th>
                        </tr>
                        @foreach($merchant as $row)
                                <tr>
                                <td>{{$row->Nama}}</td>
                                <td>{{$row->Alamat}}</td>
                                <td>
                                    <form action="{{url('/block_merchant')}}" method="post">
                                        {{csrf_field()}}
                                        <input type="hidden" name='Username' value='{{$row->Username}}'>
                                        <button type='submit' class='btn btn-outline-primary'>Block</button>
                                    </form>
                                </td>
                                </tr>
                        @endforeach
                        @foreach($block as $row)
                                <tr>
                                <td>{{$row->Nama}}</td>
                                <td>{{$row->Alamat}}</td>
                                <td>
                                    <form action="{{url('/unblock_merchant')}}" method="post">
                                        {{csrf_field()}}
                                        <input type="hidden" name='Username' value='{{$row->Username}}'>
                                        <button type='submit' class='btn btn-outline-primary'>Unblock</button>
                                    </form>
                                </td>
                                </tr>
                        @endforeach
                    </table>
				</div>
			</div>
		</div>
	</main>

    @include('inc.footer')	
</body>
</html>