Library IEEE;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

Library work;
use work.bin2ssd_pck.all;

architecture control of seg7ctrl is

begin
  
  process (mclk, reset) is
    variable select_disp : std_logic := '0';
    variable count : unsigned(20 downto 0) := (others => '0');

  begin
    if (reset = '1') then
      abcdefg <= "XXXXXXX";
      count := (others => '0');
    elsif rising_edge(mclk) then
      abcdefg <=
        bin2ssd_hex(d0) when select_disp = '1' else
        bin2ssd_hex(d1);

      -- Selects the other display or add the counter
      if (count = D"1000000") then
        select_disp := not select_disp;
        count := (others => '0');
      else
        select_disp := select_disp;
        count := count + 10;
      end if;

      c <= select_disp;
    end if;
  end process;

end architecture;

-- Telleren maa ha 20 bits for å greie aa telle til 1000000. Grunnen til at man onsker aa telle til 1000000 er at 1000000 ns gir 0.01s
-- d0 og d1 brukes for aa vise tegnene til displayene







