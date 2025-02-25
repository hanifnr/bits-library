package com.example.bitslibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.bitslibrary.Models.ApiBook;
import com.example.bitslibrary.Api.UserService;
import com.example.bitslibrary.Models.Book;
import com.example.bitslibrary.Models.BookResponse;
import com.example.bitslibrary.Models.Borrow;
import com.example.bitslibrary.Models.BorrowData;
import com.example.bitslibrary.Models.ItemPinjam;
import com.example.bitslibrary.Models.PinjamRequest;
import com.example.bitslibrary.Models.PinjamResponse;
import com.example.bitslibrary.Models.User;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PinjamActivity extends AppCompatActivity {
    private TextView txtId, txtName, txtAuthor, txtJml, txtPrice, txtQty, txtDetailName, txtSubtotal,
            txtTotal, statusN, txtTotalBuku, txtDurasi, userId, tess, subtotal;
    private ImageView imgView;
    private EditText edtTgPinjam, edtTglAkhirPinjam;
    private DatePickerDialog datePickerDialogStart, datePickerDialogEnd;
    private SimpleDateFormat dateFormat;
    private TableLayout tabLayoutCart, tabLayoutDetail;
    private CheckBox cbPinjam;

    private RelativeLayout rlCart, rlDetail;
    private int usrId;

    private Toolbar toolbar;

//    private ArrayList<Borrow> borrows = new ArrayList<>();
//    private ArrayList<BorrowData>[] borrowsD = new ArrayList[]
//    final int position;

    int count = 1;
    private Button btnAdd, btnLess, btnTambahBuku, btnDelete, btnPeminjaman;

    private DecimalFormat dformt = new DecimalFormat();

    SharedPreferences preferences;
    private static final String shared_pref_name = "myPref";
    private static final int key_usrId = 0;
    private static final String key_api = "api";

//    public PinjamActivity(int position) {
//        this.position = position;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinjam);

        initViews();

        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        btnPeminjaman.setEnabled(false);
        cbPinjam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    btnPeminjaman.setEnabled(true);
                }else {
                    btnPeminjaman.setEnabled(false);
                }
            }
        });

        preferences = getSharedPreferences(shared_pref_name, MODE_PRIVATE);
        usrId = preferences.getInt(String.valueOf(key_usrId),0);

//        userId.setText(String.valueOf(usrId));
        statusN.setText("N");

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        btnTambahBuku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(PinjamActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });

//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                count++;
//                txtJmlBuku.setText(""+count);
//            }
//        });
//        btnLess.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (count <= 1) count=1;
//                else count--;
//                txtJmlBuku.setText(""+count);
//            }
//        });

        edtTgPinjam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialogPinjam();
            }
        });
        edtTglAkhirPinjam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialogAkhirPinjam();
            }
        });

        addCart();
        fillData();

        btnPeminjaman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabLayoutCart.getChildCount() <= 0|| tabLayoutDetail.getChildCount() <= 0 ||
                        TextUtils.isEmpty(edtTgPinjam.getText().toString()) ||
                        TextUtils.isEmpty(edtTglAkhirPinjam.getText().toString())){
                    Toast.makeText(PinjamActivity.this, "Isi dengan lengkap", Toast.LENGTH_LONG).show();
                }else {
//                    Toast.makeText(PinjamActivity.this, "Berhasil pinjam", Toast.LENGTH_LONG).show();
                    postPinjam();
                }
            }
        });


    }

    public void postPinjam(){

//        for (BorrowData borrowData: CartPinjam.contentBorrow()){
//
//            Borrow borrow = new Borrow();
//            borrow.setStart_date(edtTgPinjam.getText().toString());
//            borrow.setEnd_date(edtTglAkhirPinjam.getText().toString());
//            borrow.setUsr_id(usrId);
//            borrow.setStatus(statusN.getText().toString());
//            borrow.setTotal(CartPinjam.total());
//
//
//
//            ItemPinjam itemPinjam = null;
//            double d = itemPinjam.getBook().getPrice();
//            long l = (new Double(d)).longValue();
//            double val = itemPinjam.getBook().getPrice() * itemPinjam.getQty();
//            long sub = (new Double(val)).longValue();
//            borrowData.setBook_id(itemPinjam.getBook().getId());
//            borrowData.setPrice(l);
//            borrowData.setQty(itemPinjam.getQty());
//            borrowData.setSubtotal(sub);
//
//            PinjamRequest pinjamRequest = new PinjamRequest();
//
//
//
//            pinjamRequest.setBorrow(borrow);
//            pinjamRequest.setBorrowd(borrowData);
//        }

//        borrow.setStart_date(edtTgPinjam.getText().toString());
//        borrow.setEnd_date(edtTglAkhirPinjam.getText().toString());
//        borrow.setUsr_id(usrId);
//        borrow.setStatus(statusN.getText().toString());
//        borrow.setTotal(CartPinjam.total());
//
//        PinjamRequest pinjamRequest = new PinjamRequest();
//        pinjamRequest.setBorrow(borrow);

//        for (BorrowData borrowData : CartPinjam.contentBorrow()){
//            ItemPinjam itemPinjam = null;
//            double d = itemPinjam.getBook().getPrice();
//            long l = (new Double(d)).longValue();
//            double val = itemPinjam.getBook().getPrice() * itemPinjam.getQty();
//            long sub = (new Double(val)).longValue();
//            borrowData.setBook_id(itemPinjam.getBook().getId());
//            borrowData.setPrice(l);
//            borrowData.setQty(itemPinjam.getQty());
//            borrowData.setSubtotal(sub);
//            pinjamRequest.getBorrowd().add(borrowData);
//        }

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {
                        Request.Builder builder = chain.request().newBuilder();
                        builder.addHeader("Authorization", "Bearer "+getToken());
                        return chain.proceed(builder.build());
                    }

                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new MainFragment().BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        Borrow borrow = new Borrow(
                edtTgPinjam.getText().toString(),
                edtTglAkhirPinjam.getText().toString(),
                usrId,
                statusN.getText().toString(),
                CartPinjam.total()
        );



        PinjamRequest pinjamRequest = new PinjamRequest();
        List<BorrowData> bData = new ArrayList<BorrowData>();

        for (BorrowData borrowData : CartPinjam.contentBorrow()){



//            ItemPinjam itemPinjam = null;
//            double d = itemPinjam.getBook().getPrice();
//            long l = (new Double(d)).longValue();
//            double val = itemPinjam.getBook().getPrice() * itemPinjam.getQty();
//            long sub = (new Double(val)).longValue();
//            borrowData.setBook_id(itemPinjam.getBook().getId());
//            borrowData.setPrice(l);
//            borrowData.setQty(itemPinjam.getQty());
//            borrowData.setSubtotal(sub);
//            bData.add(borrowData);
//            pinjamRequest.getBorrowd().add(borrowData);
        }

//        ItemPinjam itemPinjam = new ItemPinjam();
//        double d = txtPrice.getId();
//        long l = (new Double(d)).longValue();
//        double val = txtSubtotal.getId();
//        long sub = (new Double(val)).longValue();
        BorrowData borrowData = new BorrowData();
        borrowData.setBook_id(Integer.parseInt(txtId.getText().toString()));
        borrowData.setPrice(Integer.parseInt(txtPrice.getText().toString()));
        borrowData.setQty(Integer.parseInt(txtQty.getText().toString()));
        borrowData.setSubtotal(Integer.parseInt(subtotal.getText().toString()));
        bData.add(borrowData);

        pinjamRequest.setBorrow(borrow);
        pinjamRequest.setBorrowd(bData);

//        UserService userService = retrofit.create(UserService.class);
//        Call<Borrow> call = userService.pinjam(borrow);
//        call.enqueue(new Callback<Borrow>() {
//            @Override
//            public void onResponse(Call<Borrow> call, Response<Borrow> response) {
//                if (response.isSuccessful()){
//                    Toast.makeText(PinjamActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//
//                }else {
//                    Toast.makeText(PinjamActivity.this, "Nothing response", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Borrow> call, Throwable t) {
//                String message = t.getLocalizedMessage();
//                Toast.makeText(PinjamActivity.this, message, Toast.LENGTH_LONG).show();
//            }
//        });

        UserService userService = retrofit.create(UserService.class);
        Call<PinjamResponse> call  = userService.pinjam(pinjamRequest);
        call.enqueue(new Callback<PinjamResponse>() {
            @Override
            public void onResponse(Call<PinjamResponse> call, Response<PinjamResponse> response) {
                if (response.isSuccessful()){
                    PinjamResponse pinjamResponse = response.body();
                    if (pinjamResponse.getStatus() == true){
                        Toast.makeText(PinjamActivity.this, pinjamResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(PinjamActivity.this, pinjamResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(PinjamActivity.this, "Nothing response", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PinjamResponse> call, Throwable t) {
                String message = t.getLocalizedMessage();
                Toast.makeText(PinjamActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void addCart(){
        try {
            Intent intent = getIntent();
            Book book = (Book) intent.getSerializableExtra("bookId");
            if (!CartPinjam.isExist(book)){
                CartPinjam.insert(new ItemPinjam(book,1));
            }else {
                CartPinjam.update(book);
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("ResourceType")
    private void fillData(){
        LayoutInflater inflater = LayoutInflater.from(this);
        LayoutInflater inflaterCart = LayoutInflater.from(this);

        for (ItemPinjam itemPinjam: CartPinjam.contents()){
            TableRow tableRowCart = new TableRow(this);
            tableRowCart.setLayoutParams(new TableLayout.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));

            rlCart = (RelativeLayout) inflaterCart.inflate(R.layout.rel_cart_pinjam_selected,null);
            imgView = (ImageView) rlCart.findViewById(R.id.idImgPinjam);
            txtId = (TextView) rlCart.findViewById(R.id.idIdBook);
            txtName = (TextView) rlCart.findViewById(R.id.idNameBookPinjam);
            txtAuthor = (TextView) rlCart.findViewById(R.id.idAuthorPinjam);
            txtPrice = (TextView) rlCart.findViewById(R.id.idPricePinjam);
            txtJml = (TextView) rlCart.findViewById(R.id.idJmlPinjam);
            txtQty = (TextView) rlCart.findViewById(R.id.idTxtJumlahBuku);
            btnDelete = (Button) rlCart.findViewById(R.id.idBtnDelete);

            imgView.setImageResource(R.drawable.koala_kumal);

            txtId.setText(String.valueOf(itemPinjam.getBook().getId()));
            txtId.setTypeface(Typeface.DEFAULT);

            txtName.setText(itemPinjam.getBook().getName());
            txtName.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

            txtAuthor.setText(itemPinjam.getBook().getAuthor());
            txtAuthor.setTypeface(Typeface.DEFAULT);

            txtJml.setText("Jumlah");
            txtJml.setTypeface(Typeface.DEFAULT);

            txtPrice.setText(String.valueOf(itemPinjam.getBook().getPrice()));
            txtPrice.setTypeface(Typeface.DEFAULT);

            txtQty.setText(String.valueOf(itemPinjam.getQty()));
            txtQty.setTypeface(Typeface.DEFAULT);

            btnDelete.setBackgroundResource(R.drawable.ic_del_out);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure ? ");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CartPinjam.remove(itemPinjam.getBook());
                            tabLayoutCart.removeAllViews();
                            tabLayoutDetail.removeAllViews();
                            fillData();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });

            tableRowCart.addView(rlCart);
            tabLayoutCart.addView(tableRowCart);


            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));

            rlDetail = (RelativeLayout) inflater.inflate(R.layout.table_row_rel_detail_biaya,null);
            txtDetailName = (TextView) rlDetail.findViewById(R.id.idNameBookPinjamBiaya);
            txtSubtotal = (TextView) rlDetail.findViewById(R.id.idPricePinjam);
            subtotal = (TextView) rlDetail.findViewById(R.id.subtotalHide);


//            RelativeLayout.LayoutParams txtSubParams = (RelativeLayout.LayoutParams)txtSubtotal.getLayoutParams();
//            txtSubParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            TextView txtPcsBook = (TextView) rlDetail.findViewById(R.id.idPcsBook);
            int val = itemPinjam.getBook().getPrice() * itemPinjam.getQty();
//            txtSubtotal.setLayoutParams(txtSubParams);
            subtotal.setText(String.valueOf(val));
            txtSubtotal.setText("Rp "+dformt.format(val));
//            txtSubtotal.setPadding(500,0,50,0);
            txtDetailName.setText(itemPinjam.getBook().getName());
            txtPcsBook.setText(String.valueOf(itemPinjam.getQty())+"pcs");
            tableRow.addView(rlDetail);

            tabLayoutDetail.addView(tableRow);

        }
        txtTotalBuku.setText(String.valueOf(CartPinjam.totalBuku())+" Buku");
        txtDurasi.setText("");
        txtTotal.setText("Rp "+dformt.format(CartPinjam.total()));

    }



    private void showDateDialogPinjam() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialogStart = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                edtTgPinjam.setText(dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialogStart.show();
    }



    private void showDateDialogAkhirPinjam() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialogEnd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                edtTglAkhirPinjam.setText(dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialogEnd.show();
    }

    private String getToken(){
        preferences = getSharedPreferences(shared_pref_name, MODE_PRIVATE);
        String api_key = preferences.getString(key_api, null);
        return api_key;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews(){

        txtTotal = findViewById(R.id.idTotalPricePinjam);
        txtTotalBuku = findViewById(R.id.idTotalPcsBook);
        txtDurasi = findViewById(R.id.idDurasiPinjam);

        edtTgPinjam = findViewById(R.id.idEdtTanggalPinjam);
        edtTglAkhirPinjam = findViewById(R.id.idEdtTanggalAkhirPinjam);

        btnAdd = findViewById(R.id.idBtnAdd);
        btnLess = findViewById(R.id.idBtnLess);
        btnDelete = findViewById(R.id.idBtnDelete);
        btnPeminjaman = findViewById(R.id.idBtnPeminjaman);

        cbPinjam = findViewById(R.id.idCbPinjam);

        btnTambahBuku = findViewById(R.id.idBtnTambahBukuLain);
        tabLayoutCart = findViewById(R.id.idTabCart);
        tabLayoutDetail = findViewById(R.id.idTabLayoutDetail);
//        tabLayoutDetailRow = findViewById(R.id.idTabLayoutDetailRow);
//        tableLayout = findViewById(R.id.idTableLayout);
//        relCart = findViewById(R.id.idRelcart);

        statusN = findViewById(R.id.idStatusN);
        userId = findViewById(R.id.idUserPinjam);

        toolbar = findViewById(R.id.toolbarPinjam);
    }
}