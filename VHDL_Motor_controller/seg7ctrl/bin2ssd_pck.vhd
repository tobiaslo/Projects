Library IEEE;
use IEEE.std_logic_1164.all;

package bin2ssd_pck is
  function bin2ssd_hex(bin: std_logic_vector(3 downto 0)) return std_logic_vector;
  function bin2ssd_msg(bin: std_logic_vector(3 downto 0)) return std_logic_vector;
  
end bin2ssd_pck;

package body bin2ssd_pck is
  function bin2ssd_hex(bin: std_logic_vector(3 downto 0)) return std_logic_vector is
    variable output : std_logic_vector(6 downto 0);
  begin
    with bin select output :=
      "1111110" when X"0",
      "0110000" when X"1",
      "1101101" when X"2",
      "1111001" when X"3",
      "0110011" when X"4",
      "1011011" when X"5",
      "1011111" when X"6",
      "1110000" when X"7",
      "1111111" when X"8",
      "1110011" when X"9",
      "1110111" when X"A",
      "0011111" when X"B",
      "1001110" when X"C",
      "0111101" when X"D",
      "1001111" when X"E",
      "1000111" when X"F",
      "XXXXXXX" when others;
    return output;
  end function;

  function bin2ssd_msg(bin: std_logic_vector(3 downto 0)) return std_logic_vector is
    variable output : std_logic_vector(6 downto 0);
  begin
    with bin select output :=
      "0000000" when X"0",
      "0011110" when X"1",
      "0111100" when X"2",
      "1001111" when X"3",
      "0001110" when X"4",
      "0111101" when X"5",
      "0011101" when X"6",
      "0010101" when X"7",
      "0111011" when X"8",
      "0111110" when X"9",
      "1110111" when X"A",
      "0000101" when X"B",
      "1111011" when X"C",
      "0011100" when X"D",
      "0001101" when X"E",
      "1111111" when X"F",
      "XXXXXXX" when others;
    return output;
  end function;
end package body bin2ssd_pck;