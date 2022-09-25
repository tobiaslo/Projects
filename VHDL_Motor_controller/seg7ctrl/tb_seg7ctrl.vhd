Library IEEE;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity tb_seg7ctrl is
begin
end entity;

architecture testbench of tb_seg7ctrl is

  component seg7ctrl is
    port (
      mclk : in std_logic;
      reset : in std_logic;
      d0 : in std_logic_vector(3 downto 0);
      d1 : in std_logic_vector(3 downto 0);
      abcdefg : out std_logic_vector(6 downto 0);
      c : out std_logic
    );
  end component;

  component seg7model is
    port (
      c : in std_logic;
      abcdefg : in std_logic_vector(6 downto 0);
      disp1 : out std_logic_vector(3 downto 0);
      disp0 : out std_logic_vector(3 downto 0)
    );
  end component;

  signal tb_bin : unsigned(7 downto 0) := (others => '0');
  signal tb_reset : std_logic := '0';
  signal tb_mclk : std_logic;
  signal tb_abcdefg : std_logic_vector(6 downto 0);
  signal tb_c : std_logic;

  signal tb_disp1 : std_logic_vector(3 downto 0);
  signal tb_disp0 : std_logic_vector(3 downto 0);
  

begin

  model: entity work.seg7model(beh)
    port map (
      c => tb_c,
      abcdefg => tb_abcdefg,
      disp1 => tb_disp1,
      disp0 => tb_disp0
    );

  UUT: entity work.seg7ctrl(control)
    port map (
      mclk => tb_mclk,
      reset => tb_reset,
      d0 => std_logic_vector(tb_bin(7 downto 4)),
      d1 => std_logic_vector(tb_bin(3 downto 0)),
      abcdefg => tb_abcdefg,
      c => tb_c
    );
      
  
  P_CLK: process
  begin
    tb_mclk <= '0';
    wait for 5 ns;
    tb_mclk <= '1';
    wait for 5 ns;

  end process P_CLK;

  COUNTER: process (tb_c) is
  begin
    if rising_edge(tb_c) then
      tb_bin <= tb_bin + 5 when tb_bin < "11111111" else "00000000";
    end if;
  end process;

  tb_reset <= '0', '1' after 2 ms, '0' after 3 ms;
  

end architecture;
