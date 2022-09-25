Library IEEE;
use ieee.std_logic_1164.all;
use std.textio.all;
use ieee.numeric_std.all;

entity self_test is

  generic(
    filename : string := "/uio/hume/student-u88/tobiaslo/Documents/IN3160/oblig8/src/values.txt"; -- Fil med verdier
    --filename : string := "/home/tobiaslo/M-drive/Documents/IN3160/oblig8/src/values.txt"; -- Fil med verdier
    delay : unsigned := d"300000000" -- Eller til 3 sekunder
  );
  
  port(
    mclk : in std_logic; --100MHz
    reset : in std_logic;

    duty_cycle : out std_logic_vector(7 downto 0) -- Signed values
  );

end self_test;

architecture rtl of self_test is

  signal tick: std_logic := '0';

  type memory_array is array(19 downto 0) of
    std_logic_vector(7 downto 0);

  impure function initialize_ROM(file_name: string) return memory_array is
    file init_file: text open read_mode is file_name;
    variable current_line: line;
    variable result: memory_array;
  begin
    for i in result'reverse_range loop
      readline(init_file, current_line);
      read(current_line, result(i));
    end loop;
    return result;
  end function;

  constant ROM_DATA: memory_array := initialize_ROM(filename);

begin

  P_SECOND: process (mclk, reset) is
    variable count : unsigned(29 downto 0) := (others => '0');
  begin
    if (reset = '1') then
      count := (others => '0');
    elsif rising_edge(mclk) then
      count := count + 1;
    end if;
    
    if (count = delay) then
      count := (others => '0');
      tick <= '1';
    else
      tick <= '0';
    end if;
  end process P_SECOND;

  P_READ_ROM: process (reset, tick) is
    variable rom_line: unsigned(4 downto 0) := (others => '0');
    variable rom_data_line: std_logic_vector(7 downto 0);
  begin
    if (reset = '1') then
      rom_line := (others => '0');
    elsif rising_edge(tick) then
      rom_line := "10011" when rom_line = "10011" else rom_line + 1;
    end if;

    rom_data_line := ROM_DATA(to_integer(rom_line));
    duty_cycle <= rom_data_line;

  end process P_READ_ROM;
end architecture;
