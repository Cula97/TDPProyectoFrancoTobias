package francotobias.tdpproyecto;

public class CSVWizard {
    private String data;
    private int position = 0;
    private int columnAmount = 0;


    public CSVWizard(String data){
        this.data = data;

        for(int i = position; i < data.length(); i++) {
            position = i;

            if(this.data.charAt(i) == ',')
                columnAmount++;

            if(this.data.charAt(i) == '\n') {
                columnAmount++;
                break;
            }
        }

    }

    public boolean isFinished() {
        return position >= data.length();
    }

    public String columnValue(int column) {
        String res = "";
        int counter = 1;

        for(int i = position; i < data.length(); i++) {

            if((data.charAt(i) == ',' || data.charAt(i) == '\n') && counter == column)
                break;

            if(counter == column)
                res+=data.charAt(i);

            if(data.charAt(i) == ',')
                counter++;
        }
        return res;
    }
    public void advanceRow() {

        for(int i = position; i < data.length(); i++) {
            position = i;
            if(this.data.charAt(i) == '\n') {
                position++;
                break;
            }
        }

    }

    public void restart() {
        position = 0;
    }

    public int getColumnAmount(){ return columnAmount;}

}
